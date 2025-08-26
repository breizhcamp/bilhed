package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.infrastructure.db.model.AttendeeDataDB
import java.util.*

fun AttendeeData.toDB(id: UUID) = AttendeeDataDB(
    id = id,
    company = company,
    tShirtSize = tShirtSize,
    tShirtCut = tShirtCut,
    vegan = vegan,
    meetAndGreet = meetAndGreet,
    postalCode = postalCode,
)

fun AttendeeDataDB.toAttendeeData() = AttendeeData(
    company = company,
    tShirtSize = tShirtSize,
    tShirtCut = tShirtCut,
    vegan = vegan,
    meetAndGreet = meetAndGreet,
    postalCode = postalCode,
)