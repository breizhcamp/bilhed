package org.breizhcamp.bilhed.domain.entities

import org.breizhcamp.bilhed.application.dto.PersonDTO
import java.util.UUID

data class Person (
    val id: UUID,

    val lastname: String,
    val firstname: String,
    val status: PersonStatus,
    val telephone: String?,
    val email: String,
    val pass: PassType,
    val groupId: UUID,
    val payed: Boolean = false
) {

    fun getMailAddress() = listOf(MailAddress(email, "$firstname $lastname"))

    fun localPhone(): String? {
        return if (telephone?.startsWith("+33") == true) "0${telephone.substring(3)}" else telephone
    }

    fun toDto() = PersonDTO(
        id = id,
        lastname = lastname,
        firstname = firstname,
        status = status,
        telephone = telephone,
        email = email,
        pass = pass,
        groupId = groupId,
        payed = payed
    )
}

enum class PersonStatus {
    REGISTERED, PARTICIPANT, ATTENDEE, RELEASED, BLOCKED
}