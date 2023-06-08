package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDBStatus
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipantRepo
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.IllegalStateException

@Component
class ParticipantAdapter(
    private val participantRepo: ParticipantRepo,
): ParticipantPort {
    override fun list(): List<Participant> = participantRepo.filter(ParticipantFilter.empty()).map { it.toParticipant() }

    override fun filter(filter: ParticipantFilter): List<Participant> {
        return participantRepo.filter(filter).map { it.toParticipant() }
    }

    override fun get(id: UUID): Participant {
        return participantRepo.findParticipant(id)?.toParticipant() ?: throw EntityNotFoundException("Unable to find participant [$id]")
    }

    @Transactional
    override fun save(participant: Participant) {
        requireNotNull(participantRepo.findParticipant(participant.id)) { "Unable to update participant [${participant.id}] - Not found" }
            .update(participant)
    }

    override fun listTopDrawByPassWithLimit(pass: PassType, limit: Int): List<Participant> {
        return participantRepo.listByPass(pass, PageRequest.of(0, limit)).map { it.toParticipant() }
    }

    override fun listIdsWithNoDraw(): Map<PassType, List<UUID>> = participantRepo.listParticipantWithNoDraw()
        .groupBy({ it.pass }, { it.id })


    override fun updateDrawOrder(id: UUID, drawOrder: Int) {
        participantRepo.updateDrawOrder(id, drawOrder)
    }

    override fun getAlreadyNotifCount(): Map<PassType, Int> {
        val res = participantRepo.countAlreadyNotif().toMap()
        return PassType.values().associateWith { res[it] ?: 0 }
    }

    override fun levelUpToAttendee(id: UUID) {
        participantRepo.findParticipant(id)?.apply {
            status = ParticipantDBStatus.ATTENDEE
            participantConfirmationDate = ZonedDateTime.now()
            participantConfirmationType = ConfirmationType.MAIL
        } ?: throw EntityNotFoundException("Unable to find participant [$id]")
    }

    override fun levelUpToReleased(id: UUID) {
        participantRepo.findParticipant(id)?.apply {
            status = ParticipantDBStatus.RELEASED
            participantConfirmationDate = ZonedDateTime.now()
            participantConfirmationType = ConfirmationType.MAIL
        } ?: throw EntityNotFoundException("Unable to find participant [$id]")
    }
}

private fun ParticipantDB.update(src: Participant) = this.apply {
    participantSmsStatus = src.smsStatus
    participantNbSmsSent = src.nbSmsSent
    participantSmsError = src.smsError
    participantConfirmationLimitDate = src.confirmationLimitDate
    participantSmsConfirmSentDate = src.smsConfirmSentDate
    participantMailConfirmSentDate = src.mailConfirmSentDate

    participantConfirmationDate = src.confirmationDate
    participantConfirmationType = src.confirmationType
}

private fun ParticipantDB.toParticipant() = Participant(
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
    confirmationLimitDate = participantConfirmationLimitDate,
    smsConfirmSentDate = participantSmsConfirmSentDate,
    mailConfirmSentDate = participantMailConfirmSentDate,

    confirmationDate = participantConfirmationDate,
    confirmationType = participantConfirmationType,
)
