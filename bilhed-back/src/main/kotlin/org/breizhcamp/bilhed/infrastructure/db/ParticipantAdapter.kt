package org.breizhcamp.bilhed.infrastructure.db

import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipantRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class ParticipantAdapter(
    private val participantRepo: ParticipantRepo,
): ParticipantPort {
    override fun list(): List<Participant> = participantRepo.findAll().map { it.toParticipant() }

    override fun listIdsWithNoDraw(): Map<PassType, List<UUID>> = participantRepo.listParticipantWithNoDraw()
        .groupBy({ it.pass }, { it.id })


    override fun updateDrawOrder(id: UUID, drawOrder: Int) {
        participantRepo.updateDrawOrder(id, drawOrder)
    }
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
