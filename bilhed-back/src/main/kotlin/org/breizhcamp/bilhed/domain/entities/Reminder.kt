package org.breizhcamp.bilhed.domain.entities

import java.time.ZonedDateTime
import java.util.*

data class Reminder (
    val id: UUID = UUID.randomUUID(),
    val reminderDate: ZonedDateTime,
    val template: String,
    val method: ReminderMethod,
    val personId: UUID,
    val model: Map<String, String>,
    val origin: ReminderOrigin,
)

enum class ReminderOrigin {
    MANUAL, AUTOMATIC
}

enum class ReminderMethod {
    SMS, MAIL
}
