package org.breizhcamp.bilhed.application.dto

import java.time.ZonedDateTime

data class ParticipantConfirmInfo(
    val firstname: String,
    val confirmationLimitDate: ZonedDateTime,
)
