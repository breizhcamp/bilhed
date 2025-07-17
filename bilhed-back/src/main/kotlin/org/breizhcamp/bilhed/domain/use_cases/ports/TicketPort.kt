package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.Ticket
import org.breizhcamp.bilhed.domain.entities.TicketExportData
import java.util.*

interface TicketPort {

    fun create(participant: Person): Ticket = create(listOf(participant)).first()

    fun create(participants: List<Person>): List<Ticket>

    fun delete(attendees: List<Person>)

    fun hasTicket(id: UUID): Boolean

    fun getPayUrl(id: UUID): String

    fun getPayed(): List<UUID>

    fun getExportList(): List<TicketExportData>
}