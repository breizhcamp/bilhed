package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipationInfosDB
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB

fun ParticipationInfos.toDB(person: PersonDB) = ParticipationInfosDB(
    person = person,
    participantSmsStatus = smsStatus,
    participantNbSmsSent = nbSmsSent,
    participantSmsError = smsError,
    participantNotificationConfirmSentDate = notificationConfirmSentDate,
    participantConfirmationDate = confirmationDate
)

fun ParticipationInfosDB.toParticipationInfos() = ParticipationInfos(
    personId = person.id,
    smsStatus = participantSmsStatus,
    nbSmsSent = participantNbSmsSent,
    smsError = participantSmsError,
    notificationConfirmSentDate = participantNotificationConfirmSentDate,
    confirmationDate = participantConfirmationDate
)