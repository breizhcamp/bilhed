package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Released
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB

fun ParticipantDB.toReleased() = Released(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,
)