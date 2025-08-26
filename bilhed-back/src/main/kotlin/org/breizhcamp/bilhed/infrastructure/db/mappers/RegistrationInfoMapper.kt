package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.RegistrationInfo
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB
import org.breizhcamp.bilhed.infrastructure.db.model.RegistrationInfoDB

fun RegistrationInfo.toDB(person: PersonDB) = RegistrationInfoDB(
    person = person,
    registrationDate = registrationDate,
    registrationSmsStatus = smsStatus,
    registrationNbSmsSent = nbSmsSent,
    registrationLastSmsSentDate = lastSmsSentDate,
    registrationSmsError = smsError,
    registrationToken = token,
    registrationNbTokenTries = nbTokenTries
)

fun RegistrationInfoDB.toRegistrationInfos() = RegistrationInfo(
    personId = person.id,
    registrationDate = registrationDate,
    smsStatus = registrationSmsStatus,
    nbSmsSent = registrationNbSmsSent,
    lastSmsSentDate = registrationLastSmsSentDate,
    smsError = registrationSmsError,
    token = registrationToken,
    nbTokenTries = registrationNbTokenTries
)