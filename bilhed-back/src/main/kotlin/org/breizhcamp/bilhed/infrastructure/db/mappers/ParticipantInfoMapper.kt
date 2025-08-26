package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.ParticipationInfo
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipationInfoDB
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB

fun ParticipationInfo.toDB(person: PersonDB) = ParticipationInfoDB(
    person = person,
    participantSmsStatus = smsStatus,
    participantNbSmsSent = nbSmsSent,
    participantSmsError = smsError,
    participantNotificationConfirmSentDate = notificationConfirmSentDate,
    participantConfirmationDate = confirmationDate,
    payed = payed
)

fun ParticipationInfoDB.toParticipationInfos() = ParticipationInfo(
    personId = person.id,
    smsStatus = participantSmsStatus,
    nbSmsSent = participantNbSmsSent,
    smsError = participantSmsError,
    notificationConfirmSentDate = participantNotificationConfirmSentDate,
    confirmationDate = participantConfirmationDate,
    payed = payed
)