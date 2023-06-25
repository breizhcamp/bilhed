package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.TicketExportData
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.stereotype.Service

@Service
class TicketExport(
    private val ticketPort: TicketPort,
    private val attendeePort: AttendeePort,
) {

    fun export(): List<TicketExportData> {
        val tickets = ticketPort.getExportList()
        val attendees = attendeePort.listWithData()

        return tickets.map { ticket ->
            val attendee = attendees.find { it.first.id == ticket.id }?.second
            val noGoodies = if (attendee?.tShirtSize == "no") "true" else ticket.noGoodies
            ticket.copy(
                company = attendee?.company ?: ticket.company,
                noGoodies = noGoodies,
                tShirtSize = getExportSize(attendee?.tShirtSize) ?: ticket.tShirtSize,
                tShirtFitting = getExportCut(attendee?.tShirtCut) ?: ticket.tShirtFitting,
            )
        }
    }

    private fun getExportSize(tShirtSize: String?) = when (tShirtSize) {
        "s" -> "S"
        "m" -> "M"
        "l" -> "L"
        "xl" -> "XL"
        "xxl" -> "XXL"
        "3xl" -> "XXXL"
        else -> null
    }

    private fun getExportCut(tShirtCut: String?) = when (tShirtCut) {
        "m" -> "Homme"
        "f" -> "Femme"
        else -> null
    }

}