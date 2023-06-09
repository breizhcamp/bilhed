package org.breizhcamp.bilhed.domain.entities

import java.time.ZonedDateTime
import java.util.*

data class Attendee(
    override val id: UUID,
    override val lastname: String,
    override val firstname: String,
    override val email: String,
    override val telephone: String,
    override val pass: PassType,
    override val kids: String?,

    val confirmationLimitDate: ZonedDateTime,
    val participantConfirmationDate: ZonedDateTime,
    val payed: Boolean,

): Person()