package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.*

data class RegisteredDTO (
    override val id: UUID,

    override val lastname: String,
    override val firstname: String,
    override val email: String,
    override val telephone: String,
    override val pass: PassType,
    override val kids: String?,

    val registrationDate: ZonedDateTime,

    val smsStatus: SmsStatus,
    val nbSmsSent: Int,

    val lastSmsSentDate: ZonedDateTime?,
    val smsError: String?,
    val token: String,
    val nbTokenTries: Int,
): PersonDTO()
