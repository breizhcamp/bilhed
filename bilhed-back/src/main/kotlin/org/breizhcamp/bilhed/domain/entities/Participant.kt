package org.breizhcamp.bilhed.domain.entities

import java.time.ZonedDateTime
import java.util.*

/**
 * Person that will be drawn and asked to confirm his participation
 */
data class Participant(
    override val id: UUID,
    override val lastname: String,
    override val firstname: String,
    override val email: String,
    override val telephone: String,
    override val pass: PassType,
    override val kids: String?,

    val drawOrder: Int?,

    val smsStatus: SmsStatus = SmsStatus.NOT_SENT,
    val nbSmsSent: Int = 0,
    val smsError: String? = null,
    val smsConfirmSentDate: ZonedDateTime? = null,
    val mailConfirmSentDate: ZonedDateTime? = null,

    val confirmationDate: ZonedDateTime? = null,
    val confirmationType: ConfirmationType? = null,

    ): Person()

enum class ConfirmationType {
    SMS, MAIL
}
