package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.UUID

data class ParticipationInfosDTO (
    val personId: UUID,

    val smsStatus: SmsStatus? = null,
    val nbSmsSent: Int = 0,
    val smsError: String? = null,
    val notificationConfirmSentDate: ZonedDateTime? = null,

    val confirmationDate: ZonedDateTime? = null,
)