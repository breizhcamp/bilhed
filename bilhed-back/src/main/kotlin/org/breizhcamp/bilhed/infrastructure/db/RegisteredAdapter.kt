package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toRegistered
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDBStatus.*
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipantRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*

@Service
class RegisteredAdapter(
    private val participantRepo: ParticipantRepo,
): RegisteredPort {

    override fun list(): List<Registered> = participantRepo.listRegistered().map { it.toRegistered() }

    override fun existsEmailOrPhone(email: String, phone: String): Boolean {
        return participantRepo.countByEmailOrTelephone(email, phone) > 0
    }

    override fun existPhone(phone: String): Boolean {
        return participantRepo.countByTelephone(phone) > 0
    }

    override fun save(registered: Registered) {
        participantRepo.save(registered.toDB())
    }

    override fun get(id: UUID): Registered {
        return participantRepo.findByIdOrNull(id)?.toRegistered() ?: throw EntityNotFoundException()
    }

    override fun levelUpToParticipant(id: UUID) {
        participantRepo.findByIdOrNull(id)?.apply {
            status = PARTICIPANT
            participationDate = ZonedDateTime.now()
        }
    }

    override fun resetSmsCount(id: UUID) {
        participantRepo.resetSmsCount(id)
    }

}