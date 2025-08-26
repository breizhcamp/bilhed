package org.breizhcamp.bilhed.domain.entities

data class PersonComplete(
    val person: Person,
    val group: Group,
    val attendeeData: AttendeeData? = null,
    val notifs: List<Notification>
)
