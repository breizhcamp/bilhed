package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.UUID

data class ParticipantDTO(
    override val id: UUID,

    override val lastname: String,
    override val firstname: String,
    override val email: String,
    override val telephone: String,
    override val pass: PassType,
    override val kids: String?,

    val participationDate: ZonedDateTime,

    val drawOrder: Int?,

    val smsStatus: SmsStatus,
    val nbSmsSent: Int,
    val smsError: String?,
    val notificationConfirmSentDate: ZonedDateTime?,

    val confirmationDate: ZonedDateTime?,
): PersonDTO()
