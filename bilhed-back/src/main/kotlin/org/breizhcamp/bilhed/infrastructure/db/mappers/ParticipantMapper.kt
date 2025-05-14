package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB

fun ParticipantDB.toParticipant() = Participant(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,

    participationDate = requireNotNull(participationDate) { "Participant [$id] has no participation date" },

    drawOrder = drawOrder,

    smsStatus = participantSmsStatus ?: SmsStatus.NOT_SENT,
    nbSmsSent = participantNbSmsSent,
    smsError = participantSmsError,
    notificationConfirmDate = participantNotificationConfirmSentDate,

    confirmationDate = participantConfirmationDate,
)
