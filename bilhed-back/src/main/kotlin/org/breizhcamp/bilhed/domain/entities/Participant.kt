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

    val participationDate: ZonedDateTime,

    val drawOrder: Int?,

    val smsStatus: SmsStatus = SmsStatus.NOT_SENT,
    val nbSmsSent: Int = 0,
    val smsError: String? = null,
    val notificationConfirmDate: ZonedDateTime? = null,

    val confirmationDate: ZonedDateTime? = null,

    ): Person()
