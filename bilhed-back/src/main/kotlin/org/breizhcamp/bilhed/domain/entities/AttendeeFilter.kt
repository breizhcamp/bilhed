package org.breizhcamp.bilhed.domain.entities

import java.util.UUID

class AttendeeFilter (
    status: PersonStatus? = null,
    lastname: String? = null,
    firstname: String? = null,
    email: String? = null,
    pass: PassType? = null,
    groupId: UUID? = null,
    drawn: Boolean? = null,
    val payed: Boolean? = null,
): PersonFilter(status, lastname, firstname, email, pass, groupId, drawn)