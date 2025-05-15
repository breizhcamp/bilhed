package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.domain.entities.PassType
import java.time.ZonedDateTime
import java.util.*

open class AttendeeDTO(
    override val id: UUID,

    override val lastname: String,
    override val firstname: String,
    override val email: String,
    override val telephone: String,
    override val pass: PassType,
    override val kids: String?,

    open val participantConfirmationDate: ZonedDateTime,
    open val payed: Boolean,
): PersonDTO()
