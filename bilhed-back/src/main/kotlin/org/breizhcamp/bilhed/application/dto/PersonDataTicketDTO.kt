package org.breizhcamp.bilhed.application.dto

data class PersonDataTicketDTO(
    val hasAttendeeData: Boolean,
    val hasTicket: Boolean,
    val hasPayed: Boolean,
    val payUrl: String?
)
