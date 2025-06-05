package org.breizhcamp.bilhed.domain.entities

import java.util.UUID

class PersonFilter (
    val status: PersonStatus? = null,
    val lastname: String? = null,
    val firstname: String? = null,
    val email: String? = null,
    val pass: PassType? = null,
    val payed: Boolean? = null,
    val groupId: UUID? = null,
) {
    companion object {
        fun empty() = PersonFilter(null, null, null, null, null, null, null)
    }
}