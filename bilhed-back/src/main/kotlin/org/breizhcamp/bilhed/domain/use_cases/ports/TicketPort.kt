package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Attendee
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.Ticket
import org.breizhcamp.bilhed.domain.entities.TicketExportData
import java.util.*

interface TicketPort {

    fun create(participant: Participant): Ticket = create(listOf(participant)).first()

    fun create(participants: List<Participant>): List<Ticket>

    fun delete(attendees: List<Attendee>)

    fun hasTicket(id: UUID): Boolean

    fun getPayUrl(id: UUID): String

    fun getPayed(): List<UUID>

    fun getExportList(): List<TicketExportData>
}