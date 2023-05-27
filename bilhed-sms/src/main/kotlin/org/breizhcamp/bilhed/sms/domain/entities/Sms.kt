package org.breizhcamp.bilhed.sms.domain.entities

import java.util.*

data class Sms(
    val id: UUID,
    val phone: String,
    val template: String,
    val model: Map<String, String>,
)
