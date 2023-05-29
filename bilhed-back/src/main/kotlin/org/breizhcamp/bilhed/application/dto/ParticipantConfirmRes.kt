package org.breizhcamp.bilhed.application.dto

import java.time.ZonedDateTime

data class ParticipantConfirmRes(
    val firstname: String,
    val confirmationLimitDate: ZonedDateTime,
)
