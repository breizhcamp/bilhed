package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.application.dto.PersonDTO

data class PersonCompleteDTO(
    val person: PersonDTO,
    val group: GroupDTO,
    val attendeeData: AttendeeDataDTO? = null,
    val notifs: List<NotificationDTO>
)
