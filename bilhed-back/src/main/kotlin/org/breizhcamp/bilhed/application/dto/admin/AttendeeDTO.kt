package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.domain.entities.PassType
import java.time.ZonedDateTime
import java.util.*

data class AttendeeDTO(
    val id: UUID,
    val lastname: String,
    val firstname: String,
    val email: String,
    val telephone: String,
    val pass: PassType,
    val kids: String?,

    val participantConfirmationDate: ZonedDateTime,
    val payed: Boolean,
)
