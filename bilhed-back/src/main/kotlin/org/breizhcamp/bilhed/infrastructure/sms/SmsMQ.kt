package org.breizhcamp.bilhed.infrastructure.sms

import java.util.UUID

data class SmsMQ (
    val id: UUID,
    val phone: String,
    val template: String,
    val model: Map<String, String>
)