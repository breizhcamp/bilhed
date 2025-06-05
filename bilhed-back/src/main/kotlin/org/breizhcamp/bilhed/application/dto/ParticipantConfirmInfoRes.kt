package org.breizhcamp.bilhed.application.dto

import java.time.ZonedDateTime

data class ParticipantConfirmInfoRes(
    val groupPayment: Boolean,
    val referent: PersonDTO,
    val companions: List<PersonDTO>,
    val confirmationLimitDate: ZonedDateTime,
)
