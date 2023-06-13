package org.breizhcamp.bilhed.domain.entities

data class PersonDataTicket(
    val hasAttendeeData: Boolean,
    val hasTicket: Boolean,
    val hasPayed: Boolean,
    val payUrl: String?,
)
