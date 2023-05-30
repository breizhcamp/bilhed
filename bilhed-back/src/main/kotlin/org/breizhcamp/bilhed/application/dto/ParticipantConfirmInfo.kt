package org.breizhcamp.bilhed.application.dto

import java.time.ZonedDateTime

data class ParticipantConfirmInfo(
    val lastname: String,
    val firstname: String,
    val email: String,
    val confirmationLimitDate: ZonedDateTime,
)
