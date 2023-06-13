package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Blocked
import org.breizhcamp.bilhed.domain.entities.Released
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB

fun ParticipantDB.toBlocked() = Blocked(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,
)