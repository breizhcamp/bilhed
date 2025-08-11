package org.breizhcamp.bilhed.domain.entities

import java.time.ZonedDateTime
import java.util.*

data class Notification (
    val id: UUID = UUID.randomUUID(),
    val reminderDate: ZonedDateTime,
    val template: String,
    val method: NotifMethod,
    val personId: UUID,
    val model: Map<String, String>,
    val origin: NotifOrigin,
)

enum class NotifOrigin {
    MANUAL, AUTOMATIC
}

enum class NotifMethod {
    SMS, MAIL
}
