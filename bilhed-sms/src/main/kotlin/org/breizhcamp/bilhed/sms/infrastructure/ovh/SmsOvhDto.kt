package org.breizhcamp.bilhed.sms.infrastructure.ovh

data class SmsOvhDto(
    val serviceName: String,
    val message: String,
    val receivers: List<String>,
    val sender: String,
    val noStopClause: Boolean,
    val priority: String,
    val senderForResponse: Boolean,
    val validityPeriod: Int,
    val tag: String,
)
