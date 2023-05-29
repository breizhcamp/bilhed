package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.Ticket

interface TicketPort {

    fun create(participant: Participant): Ticket

}