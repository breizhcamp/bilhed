package org.breizhcamp.bilhed.domain.entities

import org.apache.commons.lang3.RandomStringUtils
import java.time.ZonedDateTime
import java.util.*

/**
 * Person that will be participating in the lottery
 */
data class Registered(
    override val id: UUID,

    override val lastname: String,
    override val firstname: String,
    override val email: String,
    override val telephone: String,
    override val pass: PassType,
    override val kids: String?,

    val registrationDate: ZonedDateTime = ZonedDateTime.now(),

    val smsStatus: SmsStatus = SmsStatus.NOT_SENT,
    val nbSmsSent: Int = 0,
    val lastSmsSentDate: ZonedDateTime? = null,
    val smsError: String? = null,
    val token: String = genSmsToken(),
    val nbTokenTries: Int = 0,
): Person() {
    companion object {
        private fun genSmsToken(): String = RandomStringUtils.randomNumeric(6)
    }
}
