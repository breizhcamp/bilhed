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
            throw IllegalStateException("Vous avez dépassé la date limite de confirmation, votre place a été remise en jeu")
        }
        return p
    }

    @Transactional
    fun confirm(id: UUID, data: AttendeeData): Ticket {
        val p = get(id)
        logger.info { "Create ticket for participant [$id] / [${p.lastname}] [${p.firstname}]" }
        participantPort.levelUpToAttendee(id)
        attendeePort.saveData(id, data)
        val ticket = ticketPort.create(p)
        logger.info { "Ticket created for participant [$id] / [${p.lastname}] [${p.firstname}]" }
        return ticket
    }

}