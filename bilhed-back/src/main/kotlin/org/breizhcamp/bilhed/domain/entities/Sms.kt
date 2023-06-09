package org.breizhcamp.bilhed.domain.entities

import java.util.*

data class Sms(
    val id: UUID,
    val phone: String,
    val template: String,
    val model: Map<String, String>,
) {
    constructor(phone: String, template: String, model: Map<String, String>): this(UUID.randomUUID(), phone, template, model)
}
