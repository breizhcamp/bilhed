package org.breizhcamp.bilhed.domain.entities

import java.time.ZonedDateTime
import java.util.UUID

data class ParticipantConfirmInfo(
    val members: List<Person>,
    val refId: UUID,
    val confirmationLimitDate: ZonedDateTime,
    val pass: PassType
)
