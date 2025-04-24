package org.breizhcamp.bilhed.domain.entities

import java.util.UUID

data class ReminderConfig (
    val id: UUID,
    val type: String,
    val hours: Int,
    val templateMail: String,
    val templateSms: String,
)