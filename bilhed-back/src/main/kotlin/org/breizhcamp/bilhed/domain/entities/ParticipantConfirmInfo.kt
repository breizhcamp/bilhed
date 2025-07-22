package org.breizhcamp.bilhed.domain.entities

import java.time.ZonedDateTime

data class ParticipantConfirmInfo(
    val members: List<Person>,
    val confirmationLimitDate: ZonedDateTime,
)
