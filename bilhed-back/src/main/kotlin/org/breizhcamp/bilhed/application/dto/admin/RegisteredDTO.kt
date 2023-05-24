package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.*

data class RegisteredDTO (
    val id: UUID,

    val lastname: String,
    val firstname: String,
    val email: String,
    val telephone: String,
    val pass: PassType,
    val kids: String?,

    val registrationDate: ZonedDateTime,

    val smsStatus: SmsStatus,
    val nbSmsSent: Int,

    val lastSmsSentDate: ZonedDateTime?,
    val smsError: String?,
    val token: String,
    val nbTokenTries: Int,
)
