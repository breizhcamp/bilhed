package org.breizhcamp.bilhed.infrastructure.sms

import java.util.*

data class Sms(
    val id: UUID,
    val phone: String,
    val message: String,
)
