package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.domain.entities.PassType
import java.time.ZonedDateTime
import java.util.*

class AttendeeDataDTO (
    override val id: UUID,

    override val lastname: String,
    override val firstname: String,
    override val email: String,
    override val telephone: String,
    override val pass: PassType,
    override val kids: String?,

    override val participantConfirmationDate: ZonedDateTime,
    override val payed: Boolean,

    val company: String?,
    val tShirtSize: String,
    val tShirtCut: String?,
    val vegan: Boolean,
    val meetAndGreet: Boolean,
    val postalCode: String?,
): AttendeeDTO(id, lastname, firstname, email, telephone, pass, kids, participantConfirmationDate, payed) {
    constructor(
        attendeeDTO: AttendeeDTO,
        company: String?,
        tShirtSize: String,
        tShirtCut: String?,
        vegan: Boolean,
        meetAndGreet: Boolean,
        postalCode: String?
    ) : this(
        attendeeDTO.id, attendeeDTO.lastname, attendeeDTO.firstname, attendeeDTO.email, attendeeDTO.telephone,
        attendeeDTO.pass, attendeeDTO.kids, attendeeDTO.participantConfirmationDate, attendeeDTO.payed,
        company, tShirtSize, tShirtCut, vegan, meetAndGreet, postalCode
    )


}