package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class AttendeeSyncPayed(
    private val attendeePort: AttendeePort,
    private val ticketPort: TicketPort,
) {

    @Transactional
    fun sync() {
        logger.info { "Syncing payed status" }
        val payed = ticketPort.getPayed()

        if (payed.isEmpty()) {
            logger.info { "No attendee payed" }
        } else {
            logger.info { "Setting [${payed.size}] attendees to payed" }
            attendeePort.setPayed(payed)
        }
    }

}