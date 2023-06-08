package org.breizhcamp.bilhed.domain.entities

import org.breizhcamp.bilhed.domain.entities.PassType

data class ParticipantFilter(
    val lastname: String?,
    val firstname: String?,
    val email: String?,
    val pass: PassType?,
    val success: Boolean?,
    val payed: Boolean?,
) {
    companion object {
        fun empty() = ParticipantFilter(null, null, null, null, null, null)
    }
}