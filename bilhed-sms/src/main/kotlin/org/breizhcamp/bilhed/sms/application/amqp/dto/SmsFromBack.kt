package org.breizhcamp.bilhed.sms.application.amqp.dto

data class SmsFromBack(
    val phone: String,
    val message: String,
)
