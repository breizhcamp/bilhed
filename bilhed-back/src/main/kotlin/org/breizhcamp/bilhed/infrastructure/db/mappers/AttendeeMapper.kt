package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Attendee
import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.infrastructure.db.model.AttendeeDataDB
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
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

fun ParticipantDB.toAttendee() = Attendee(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,

    participantConfirmationDate = requireNotNull(participantConfirmationDate) { "Attendee [$id] has no confirmation date" },
    participantNotificationConfirmDate = requireNotNull(participantNotificationConfirmSentDate) { "Attendee [$id] has no notification confirmation date" },
    payed = payed,
)