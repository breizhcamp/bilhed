package org.breizhcamp.bilhed.domain.use_cases

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.PersonDataTicket
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PersonDataTicketInfo(
    private val attendeePort: AttendeePort,
    private val ticketPort: TicketPort,
) {

    /** Retrieve some infos about the person attendee data and ticket status */
    fun getInfos(id: UUID): PersonDataTicket {
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