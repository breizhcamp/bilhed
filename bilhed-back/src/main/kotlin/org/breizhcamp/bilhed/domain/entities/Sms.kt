package org.breizhcamp.bilhed.domain.entities

import java.util.*

data class Sms(
    val id: UUID,
    val phone: String,
    val message: String,
)
