package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipationInfoPort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class AttendeeSyncPayed(
    private val ticketPort: TicketPort,
    private val partInfoPort: ParticipationInfoPort,
) {

    @Transactional
    fun sync() {
        logger.info { "Syncing payed status" }
        val payed = ticketPort.getPayed()

        if (payed.isEmpty()) {
            logger.info { "No attendee payed" }
        } else {
            logger.info { "Setting [${payed.size}] attendees to payed" }
            partInfoPort.setPayed(payed)
        }
    }

}