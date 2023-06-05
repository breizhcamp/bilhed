package org.breizhcamp.bilhed.application.dto

import org.breizhcamp.bilhed.domain.entities.PassType
import java.time.ZonedDateTime

data class ParticipantConfirmInfo(
    val lastname: String,
    val firstname: String,
    val email: String,
    val pass: PassType,
    val confirmationLimitDate: ZonedDateTime,
)
