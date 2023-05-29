package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.domain.entities.ConfirmationType
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.UUID

data class ParticipantDTO(
    val id: UUID,
    val lastname: String,
    val firstname: String,
    val email: String,
    val telephone: String,
    val pass: PassType,
    val kids: String?,

    val participationDate: ZonedDateTime,

    val drawOrder: Int?,

    val smsStatus: SmsStatus,
    val nbSmsSent: Int,
    val smsError: String?,
    val smsConfirmSentDate: ZonedDateTime?,
    val mailConfirmSentDate: ZonedDateTime?,

    val confirmationLimitDate: ZonedDateTime? = null,
    val confirmationDate: ZonedDateTime?,
    val confirmationType: ConfirmationType?,
)
