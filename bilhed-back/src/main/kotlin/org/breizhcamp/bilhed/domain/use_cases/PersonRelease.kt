package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.ConfigPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class PersonRelease(
    private val attendeePort: AttendeePort,
    private val participantPort: ParticipantPort,
    private val configPort: ConfigPort,
    private val ticketPort: TicketPort,
) {

    @Transactional
    fun attendeeRelease(ids: List<UUID>) {
        val attendees = attendeePort.get(ids)
        logger.info { "Release [${attendees.size}] attendees: " + attendees.joinToString { "${it.lastname} ${it.firstname}" } }
        ticketPort.delete(attendees)
        attendees.forEach { attendeePort.levelUpToReleased(it.id) }
        logger.info { "[${attendees.size}] Attendees released"}
    }

    @Transactional
    fun participantReleaseAuto() {
        val timeReminderPar = configPort.get("reminderTimePar")
        participantPort.list().forEach {
            val participationDate = it.participationDate.truncatedTo(ChronoUnit.MINUTES)
            val now = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            if (participationDate.plusHours(timeReminderPar.value.toLong()).isEqual(now)) {
                participantPort.levelUpToReleased(it.id)
            }
        }
    }

    @Transactional
    fun attendeeReleaseAuto() {
        val ids = mutableListOf<UUID>()
        val timeReminderAtt = configPort.get("reminderTimeAtt")
        attendeePort.list().forEach {
            val participantConfirmDate = it.participantConfirmationDate.truncatedTo(ChronoUnit.MINUTES)
            val now = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            if (participantConfirmDate.plusHours(timeReminderAtt.value.toLong()).isEqual(now) && !it.payed) {
                ids.add(it.id)
            }
        }
        if (!ids.isEmpty()) {
            attendeeRelease(ids)
        }
    }
}