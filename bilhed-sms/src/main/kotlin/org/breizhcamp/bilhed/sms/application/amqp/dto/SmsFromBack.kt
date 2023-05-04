package org.breizhcamp.bilhed.sms.application.amqp.dto

import java.util.*

data class SmsFromBack(
    val id: UUID,
    val phone: String,
    val message: String,
)
