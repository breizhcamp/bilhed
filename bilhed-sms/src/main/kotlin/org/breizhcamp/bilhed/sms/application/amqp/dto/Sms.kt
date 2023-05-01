package org.breizhcamp.bilhed.sms.application.amqp.dto

data class Sms(
    val phone: String,
    val message: String,
)
