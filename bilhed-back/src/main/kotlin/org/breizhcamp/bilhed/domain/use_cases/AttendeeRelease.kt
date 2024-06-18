package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class AttendeeRelease(
    private val attendeePort: AttendeePort,
    private val ticketPort: TicketPort,
) {

    @Transactional
    fun release(ids: List<UUID>) {
        val attendees = attendeePort.get(ids)
        logger.info { "Release [${attendees.size}] attendees: " + attendees.joinToString { "${it.lastname} ${it.firstname}" } }
        ticketPort.delete(attendees)
        attendees.forEach { attendeePort.levelUpToReleased(it.id) }
        logger.info { "[${attendees.size}] Attendees released"}
    }
}