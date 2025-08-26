package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.Ticket
import org.breizhcamp.bilhed.domain.entities.TicketExportData
import java.util.*

interface TicketPort {

    fun create(participant: Person, pass: PassType): Ticket = create(listOf(participant), pass).first()

    fun create(participants: List<Person>, pass: PassType): List<Ticket>

    fun delete(attendees: List<Person>)

    fun hasTicket(id: UUID): Boolean

    fun getPayUrl(id: UUID): String

    fun getPayed(): List<UUID>

    fun getExportList(): List<TicketExportData>
}