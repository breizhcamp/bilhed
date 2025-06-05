package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.ReferentInfos
import org.breizhcamp.bilhed.infrastructure.db.model.ReferentInfosDB

fun ReferentInfos.toDB() = ReferentInfosDB(
    personId = personId,
    registrationDate = registrationDate,
    registrationSmsStatus = smsStatus,
    registrationNbSmsSent = nbSmsSent,
    registrationLastSmsSentDate = lastSmsSentDate,
    registrationSmsError = smsError,
    registrationToken = token,
    registrationNbTokenTries = nbTokenTries
)

fun ReferentInfosDB.toReferentInfos() = ReferentInfos(
    personId = personId,
    registrationDate = registrationDate,
    smsStatus = registrationSmsStatus,
    nbSmsSent = registrationNbSmsSent,
    lastSmsSentDate = registrationLastSmsSentDate,
    smsError = registrationSmsError,
    token = registrationToken,
    nbTokenTries = registrationNbTokenTries
)