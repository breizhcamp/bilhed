package org.breizhcamp.bilhed.domain.use_cases

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.PersonDataTicket
import org.breizhcamp.bilhed.domain.entities.Released
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class PersonDataTicketInfo(
    private val attendeePort: AttendeePort,
    private val ticketPort: TicketPort,
    private val personPort: PersonPort,
) {

    /** Retrieve some infos about the person attendee data and ticket status */
    fun getInfos(id: UUID): PersonDataTicket? {
        if (personPort.get(id) is Released) return null

        val hasData = attendeePort.getData(id) != null
        val hasTicket = ticketPort.hasTicket(id)

        val payed = try {
            attendeePort.get(id).payed
        } catch (e: EntityNotFoundException) {
            false
        }

        val payUrl = try {
            ticketPort.getPayUrl(id)
        } catch (e: EntityNotFoundException) {
            null
        }

        return PersonDataTicket(hasData, hasTicket, payed, payUrl)
    }

}