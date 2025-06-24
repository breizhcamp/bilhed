package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipationInfosPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toParticipationInfos
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipationInfosRepo
import org.breizhcamp.bilhed.infrastructure.db.repos.PersonRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
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
        // TODO : voir avec Alex -> impossible de save une copy dans un @Transactional
        val partInfosDB = partInfosRepo.findByIdOrNull(partInfos.personId)

        if (partInfosDB == null) { // cas d'usage normal
            partInfosRepo.save(partInfos.toDB(personRepo.getReferenceById(partInfos.personId)))
            return
        }

        with(partInfosDB) {
            participantNotificationConfirmSentDate = partInfos.notificationConfirmSentDate
            participantConfirmationDate = partInfos.confirmationDate
            participantNbSmsSent = partInfos.nbSmsSent
            participantSmsError = partInfos.smsError
            participantSmsStatus = partInfos.smsStatus
        }
        partInfosRepo.save(partInfosDB)
    }

    override fun list(): List<ParticipationInfos> {
        return partInfosRepo.findAll().map { it.toParticipationInfos() }
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

}