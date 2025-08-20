package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.domain.entities.NotifMethod
import org.breizhcamp.bilhed.domain.entities.NotifOrigin
import java.time.ZonedDateTime
import java.util.UUID

data class NotificationDTO(
    val id: UUID = UUID.randomUUID(),
    val reminderDate: ZonedDateTime,
    val template: String,
    val method: NotifMethod,
    val personId: UUID,
    val model: Map<String, String>,
    val origin: NotifOrigin,
)