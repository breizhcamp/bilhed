package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.ParticipantFilter
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toParticipant
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDBStatus
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipantRepo
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

@Component
class ParticipantAdapter(
    private val participantRepo: ParticipantRepo,
): ParticipantPort {
    override fun list(): List<Participant> = participantRepo.filterParticipant(ParticipantFilter.empty()).map { it.toParticipant() }

    override fun filter(filter: ParticipantFilter): List<Participant> {
        return participantRepo.filterParticipant(filter).map { it.toParticipant() }
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

    override fun levelUpToAttendee(id: UUID): Participant {
        return participantRepo.findParticipant(id)?.apply {
            status = ParticipantDBStatus.ATTENDEE
            participantConfirmationDate = ZonedDateTime.now()
        }?.toParticipant() ?: throw EntityNotFoundException("Unable to find participant [$id]")
    }

    override fun levelUpToReleased(id: UUID) {
        participantRepo.findParticipant(id)?.apply {
            status = ParticipantDBStatus.RELEASED
            participantConfirmationDate = ZonedDateTime.now()
        } ?: throw EntityNotFoundException("Unable to find participant [$id]")
    }
}

private fun ParticipantDB.update(src: Participant) = this.apply {
    participantSmsStatus = src.smsStatus
    participantNbSmsSent = src.nbSmsSent
    participantSmsError = src.smsError
    participantNotificationConfirmSentDate = src.notificationConfirmDate

    participantConfirmationDate = src.confirmationDate
}
