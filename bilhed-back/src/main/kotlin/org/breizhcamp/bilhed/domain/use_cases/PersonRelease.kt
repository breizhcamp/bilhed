package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.ConfigPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.breizhcamp.bilhed.infrastructure.TimeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class PersonRelease(
    private val attendeePort: AttendeePort,
    private val participantPort: ParticipantPort,
    private val configPort: ConfigPort,
    private val ticketPort: TicketPort,
    private val timeService: TimeService,
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
        val timeReminderPar = configPort.get("reminderTimePar").value.toLong()
        val now = timeService.now()
        participantPort.list().forEach {
            val deadline = it.participationDate.plusHours(timeReminderPar)
            if (deadline.isBefore(now)) {
                participantPort.levelUpToReleased(it.id)
                logger.info { "Participant [${it.lastname} ${it.firstname}] released, limit was $deadline" }
            }
        }
    }

    @Transactional
    fun attendeeReleaseAuto() {
        val timeReminderAtt = configPort.get("reminderTimeAtt").value.toLong()
        val now = timeService.now()

        val ids: List<UUID> = attendeePort.list().mapNotNull {
            val deadline = it.participantConfirmationDate.plusHours(timeReminderAtt)
            if (!it.payed && deadline.isBefore(now)) it.id else null
        }

        attendeeRelease(ids)
    }
}