package org.breizhcamp.bilhed.application.dto

import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.*

data class ReferentInfosDTO(
    val personId: UUID,
    val registrationDate: ZonedDateTime,

    val smsStatus: SmsStatus,
    val nbSmsSent: Int,
    val lastSmsSentDate: ZonedDateTime?,
    val smsError: String? ,
    val token: String,
    val nbTokenTries: Int,
)
