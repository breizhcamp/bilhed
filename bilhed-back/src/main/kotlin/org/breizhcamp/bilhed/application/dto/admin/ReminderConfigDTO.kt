package org.breizhcamp.bilhed.application.dto.admin

import java.util.UUID

data class ReminderConfigDTO (
    val id: UUID,
    val type: String,
    val hours: Int,
    val templateMail: String,
    val templateSms: String,
)