package org.breizhcamp.bilhed.application.dto

import org.breizhcamp.bilhed.domain.entities.PassType
import java.time.ZonedDateTime
import java.util.UUID

data class ParticipantConfirmInfoRes(
    val members: List<PersonDTO>,
    val refId: UUID,
    val confirmationLimitDate: ZonedDateTime,
    val pass: PassType
)
