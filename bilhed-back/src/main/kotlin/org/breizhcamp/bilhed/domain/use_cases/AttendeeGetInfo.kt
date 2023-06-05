package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class AttendeeGetInfo(
    private val ticketPort: TicketPort,
) {

    fun getPaymentUrl(id: UUID): String = ticketPort.getPayUrl(id)

}