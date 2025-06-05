package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipationInfosDB

fun ParticipationInfos.toDB() = ParticipationInfosDB(
    personId = personId,
    participantSmsStatus = smsStatus,
    participantNbSmsSent = nbSmsSent,
    participantSmsError = smsError,
    participantNotificationConfirmSentDate = notificationConfirmSentDate,
    participantConfirmationDate = confirmationDate
)

fun ParticipationInfosDB.toParticipationInfos() = ParticipationInfos(
    personId = personId,
    smsStatus = participantSmsStatus,
    nbSmsSent = participantNbSmsSent,
    smsError = participantSmsError,
    notificationConfirmSentDate = participantNotificationConfirmSentDate,
    confirmationDate = participantConfirmationDate
)