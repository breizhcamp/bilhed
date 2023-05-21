package org.breizhcamp.bilhed.infrastructure.db

import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipantRepo
import org.springframework.stereotype.Component

@Component
class ParticipantAdapter(
    private val participantRepo: ParticipantRepo,
): ParticipantPort {
    override fun list(): List<Participant> = participantRepo.findAll().map { it.toParticipant() }
}

private fun ParticipantDB.toParticipant() = Participant(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,

    drawOrder = null,

    smsStatus = participantSmsStatus ?: SmsStatus.NOT_SENT,
    nbSmsSent = participantNbSmsSent,
    smsError = participantSmsError,
    smsConfirmSentDate = participantSmsConfirmSentDate,
    mailConfirmSentDate = participantMailConfirmSentDate,

    confirmationDate = participantConfirmationDate,
    confirmationType = participantConfirmationType,
)
