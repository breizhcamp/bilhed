package org.breizhcamp.bilhed.domain.entities

import java.time.ZonedDateTime

data class ParticipantConfirmInfo(
    val groupPayment: Boolean,
    val referent: Person,
    val companions: List<Person>,
    val confirmationLimitDate: ZonedDateTime,
)
