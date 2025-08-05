package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.RegistrationInfos
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB
import org.breizhcamp.bilhed.infrastructure.db.model.RegistrationInfosDB

fun RegistrationInfos.toDB(person: PersonDB) = RegistrationInfosDB(
    person = person,
    registrationDate = registrationDate,
    registrationSmsStatus = smsStatus,
    registrationNbSmsSent = nbSmsSent,
    registrationLastSmsSentDate = lastSmsSentDate,
    registrationSmsError = smsError,
    registrationToken = token,
    registrationNbTokenTries = nbTokenTries
)

fun RegistrationInfosDB.toRegistrationInfos() = RegistrationInfos(
    personId = person.id,
    registrationDate = registrationDate,
    smsStatus = registrationSmsStatus,
    nbSmsSent = registrationNbSmsSent,
    lastSmsSentDate = registrationLastSmsSentDate,
    smsError = registrationSmsError,
    token = registrationToken,
    nbTokenTries = registrationNbTokenTries
)