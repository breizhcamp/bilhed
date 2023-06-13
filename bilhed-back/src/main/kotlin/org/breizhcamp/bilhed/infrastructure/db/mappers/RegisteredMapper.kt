package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDBStatus

fun Registered.toDB() = ParticipantDB(
    id = id,
    status = ParticipantDBStatus.REGISTERED,
    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,
    registrationDate = registrationDate,
    registrationSmsStatus = smsStatus,
    registrationNbSmsSent = nbSmsSent,
    registrationLastSmsSentDate = lastSmsSentDate,
    registrationSmsError = smsError,
    registrationToken = token,
    registrationNbTokenTries = nbTokenTries,
)

fun ParticipantDB.toRegistered() = Registered(id, lastname, firstname, email, telephone, pass, kids, registrationDate,
    registrationSmsStatus, registrationNbSmsSent, registrationLastSmsSentDate, registrationSmsError, registrationToken,
    registrationNbTokenTries)