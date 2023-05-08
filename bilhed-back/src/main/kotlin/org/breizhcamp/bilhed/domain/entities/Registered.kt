package org.breizhcamp.bilhed.domain.entities

import java.time.ZonedDateTime
import java.util.*

/**
 * Person that will be participating in the lottery
 */
data class Registered(
    val id: UUID,

    val lastname: String,
    val firstname: String,
    val email: String,
    val telephone: String,

    val registrationDate: ZonedDateTime = ZonedDateTime.now(),

    val smsStatus: SmsStatus = SmsStatus.NOT_SENT,
    val nbSmsSent: Int = 0,
    val lastSmsSentDate: ZonedDateTime? = null,
    val smsError: String? = null,
    val token: String? = null,
    val nbTokenTries: Int = 0,
) {
    fun localPhone(): String {
        return if (telephone.startsWith("+33")) "0${telephone.substring(3)}" else telephone
    }
}
