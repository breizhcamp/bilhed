package org.breizhcamp.bilhed.application.dto

import java.time.ZonedDateTime

data class ParticipantConfirmInfoRes(
    val members: List<PersonDTO>,
    val confirmationLimitDate: ZonedDateTime,
)
