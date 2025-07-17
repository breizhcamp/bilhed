package org.breizhcamp.bilhed.application.dto

import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import java.util.UUID

data class PersonDTO(
    val id: UUID,

    val lastname: String,
    val firstname: String,
    val status: PersonStatus,
    val email: String,
    val telephone: String?,
    val pass: PassType,
    val groupId: UUID,
    val payed: Boolean = false
)
