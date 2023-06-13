package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.Ticket
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException
import java.time.ZonedDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class ParticipantConfirm(
    private val participantPort: ParticipantPort,
    private val ticketPort: TicketPort,
    private val attendeePort: AttendeePort,
) {

    fun get(id: UUID): Participant {
        val p = participantPort.get(id)
        val limitDate = requireNotNull(p.confirmationLimitDate) { "Vous n'avez pas été tiré au sort" }
        if (limitDate.isBefore(ZonedDateTime.now())) {
            throw IllegalArgumentException("Vous avez dépassé la date limite de confirmation, votre place a été remise en jeu")
        }
        return p
    }

    @Transactional
    fun confirm(id: UUID, data: AttendeeData): Ticket {
        logger.info { "Level up participant to attendee [$id]" }
        val p = participantPort.levelUpToAttendee(id)
        logger.info { "Save attendee data for participant [$id] / [${p.lastname}] [${p.firstname}]" }
        attendeePort.saveData(id, data)
        logger.info { "Create ticket for participant [$id] / [${p.lastname}] [${p.firstname}]" }
        val ticket = ticketPort.create(p)
        logger.info { "Ticket created for participant [$id] / [${p.lastname}] [${p.firstname}]" }
        return ticket
    }

    @Transactional
    fun confirmList(ids: List<UUID>): List<Ticket> {
        val participants = ids.map {
            logger.info { "Level up participant to attendee [$it]" }
            participantPort.levelUpToAttendee(it).also { p ->
                logger.info { "Participant [$it] / [${p.lastname}] [${p.firstname}] leveled up to attendee" }
            }
        }

        logger.info { "Create tickets for [${participants.size}] participants" }
        val tickets = ticketPort.create(participants)
        logger.info { "[${tickets.size}] tickets created for [${participants.size}] participants" }
        return tickets
    }



    @Transactional
    fun cancel(id: UUID) {
        val p = get(id)
        logger.info { "Level up participant to release [$id] / [${p.lastname}] [${p.firstname}]" }
        participantPort.levelUpToReleased(id)
    }

    @Transactional
    fun release(ids: List<UUID>) = ids.forEach {
        logger.info { "Level up participant to release [$it]" }
        participantPort.levelUpToReleased(it)
    }
}