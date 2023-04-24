package org.breizhcamp.bilhed.domain.entities

import java.time.Instant

/**
 *
 */
data class Registered(
    val id: String,

    val lastname: String,
    val firstname: String,
    val email: String,
    val telephone: String,

    val registrationDate: Instant = Instant.now(),

    val smsStatus: SmsStatus = SmsStatus.NOT_SENT,
    val nbSmsSent: Int = 0,
    val lastSmsSentDate: Instant? = null,
)
