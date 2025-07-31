package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.ReferentInfos
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB
import org.breizhcamp.bilhed.infrastructure.db.model.ReferentInfosDB

fun ReferentInfos.toDB(person: PersonDB) = ReferentInfosDB(
    person = person,
    registrationDate = registrationDate,
    registrationSmsStatus = smsStatus,
    registrationNbSmsSent = nbSmsSent,
    registrationLastSmsSentDate = lastSmsSentDate,
    registrationSmsError = smsError,
    registrationToken = token,
    registrationNbTokenTries = nbTokenTries
)

fun ReferentInfosDB.toReferentInfos() = ReferentInfos(
    personId = person.id,
    registrationDate = registrationDate,
    smsStatus = registrationSmsStatus,
    nbSmsSent = registrationNbSmsSent,
    lastSmsSentDate = registrationLastSmsSentDate,
    smsError = registrationSmsError,
    token = registrationToken,
    nbTokenTries = registrationNbTokenTries
)