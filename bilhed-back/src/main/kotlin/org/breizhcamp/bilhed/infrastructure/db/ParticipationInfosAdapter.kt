package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipationInfosPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toParticipationInfos
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipationInfosRepo
import org.breizhcamp.bilhed.infrastructure.db.repos.PersonRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.util.*

@Component
class ParticipationInfosAdapter(
    val partInfosRepo: ParticipationInfosRepo,
    private val personRepo: PersonRepo
): ParticipationInfosPort {
    override fun get(id: UUID): ParticipationInfos {
        return partInfosRepo.findByIdOrNull(id)?.toParticipationInfos() ?: throw EntityNotFoundException("ParticipationInfos of [$id] Not Found")
    }

    override fun get(ids: List<UUID>): List<ParticipationInfos> {
        return partInfosRepo.findAllById(ids).map { it.toParticipationInfos() }
    }

    override fun save(partInfos: ParticipationInfos) {
        partInfosRepo.save(partInfos.toDB(personRepo.getReferenceById(partInfos.personId)))
    }

    override fun list(status: PersonStatus): List<ParticipationInfos> {
        return partInfosRepo.findAllByStatus(status.toDB()).map { it.toParticipationInfos() }
    }

    override fun getByGroup(id: UUID): List<ParticipationInfos> {
        return partInfosRepo.findByGroupId(id).map { it.toParticipationInfos() }
    }

    override fun getByGroups(ids: List<UUID>): List<ParticipationInfos> {
        return partInfosRepo.findAllByGroupIds(ids).map { it.toParticipationInfos() }
    }

    override fun existsByPersonId(id: UUID): Boolean {
        return partInfosRepo.existsById(id)
    }

    override fun updateConfirmationDate(id: UUID, confirmationDate: ZonedDateTime) {
        val partInfosDB = partInfosRepo.findByIdOrNull(id) ?: throw EntityNotFoundException("ParticipationInfos of [$id] Not Found")
        partInfosDB.apply {
            participantConfirmationDate = confirmationDate
        }
    }

    override fun updateSms(id: UUID, smsStatus: SmsStatus, error: String?) {
        val partInfosDB = partInfosRepo.findByIdOrNull(id) ?: throw EntityNotFoundException("ParticipationInfos of [$id] Not Found")
        partInfosDB.apply {
            participantSmsStatus = smsStatus
            participantSmsError = error
        }

    }

    override fun updateNotification(id: UUID, notificationDate: ZonedDateTime) {
        val partInfosDB = partInfosRepo.findByIdOrNull(id) ?: throw EntityNotFoundException("ParticipationInfos of [$id] Not Found")
        partInfosDB.apply {
            participantNotificationConfirmSentDate = notificationDate
        }
        partInfosRepo.save(partInfosDB)
    }

}