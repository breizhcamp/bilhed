package org.breizhcamp.bilhed.domain.entities

import java.time.ZonedDateTime
import java.util.*

data class Reminder (
    val id: UUID,
    val reminderDate: ZonedDateTime,
    val template: String,
    val method: ReminderMethod,
    val personId: UUID,
    val model: Map<String, String>,
    val origin: ReminderOrigin,
) {
    constructor(reminderDate: ZonedDateTime, template: String, method: ReminderMethod, personId: UUID,
                model: Map<String, String>, origin: ReminderOrigin) : this(UUID.randomUUID(), reminderDate, template, method, personId, model, origin)
}

enum class ReminderOrigin {
    MANUAL, AUTOMATIC
}

enum class ReminderMethod {
    SMS, MAIL
}
