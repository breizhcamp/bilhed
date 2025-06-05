package org.breizhcamp.bilhed.domain.entities

import java.time.ZonedDateTime
import java.util.UUID

data class ParticipationInfos(
    val personId: UUID,

    val smsStatus: SmsStatus? = null,
    val nbSmsSent: Int = 0,
    val smsError: String? = null,
    val notificationConfirmSentDate: ZonedDateTime? = null,

    val confirmationDate: ZonedDateTime? = null,
)
