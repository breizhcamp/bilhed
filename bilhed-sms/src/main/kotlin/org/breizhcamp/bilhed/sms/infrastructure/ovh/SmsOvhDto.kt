package org.breizhcamp.bilhed.sms.infrastructure.ovh

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SmsOvhDto(
    val message: String,
    val receivers: List<String>,
    val sender: String?,
    val noStopClause: Boolean,
    val priority: String,
    val senderForResponse: Boolean,
    val validityPeriod: Int,
    val tag: String,
)
