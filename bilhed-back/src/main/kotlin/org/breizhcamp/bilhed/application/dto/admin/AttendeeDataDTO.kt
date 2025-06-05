package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.application.dto.PersonDTO

class AttendeeDataDTO (
    val attendee: PersonDTO,

    val company: String?,
    val tShirtSize: String,
    val tShirtCut: String?,
    val vegan: Boolean,
    val meetAndGreet: Boolean,
    val postalCode: String?,
)