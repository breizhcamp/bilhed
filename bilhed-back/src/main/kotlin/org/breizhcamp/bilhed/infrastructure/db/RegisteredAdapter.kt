package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
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
        return participantRepo.findByIdOrNull(id)?.toModel() ?: throw EntityNotFoundException()
    }

    override fun levelUpToParticipant(id: UUID) {
        participantRepo.findByIdOrNull(id)?.apply {
            status = PARTICIPANT
            participationDate = ZonedDateTime.now()
        }
    }


    fun Registered.toDB() = ParticipantDB(
        id = id,
        status = REGISTERED,
        lastname = lastname,
        firstname = firstname,
        email = email,
        telephone = telephone,
        registrationDate = registrationDate,
        registrationSmsStatus = smsStatus,
        registrationNbSmsSent = nbSmsSent,
        registrationLastSmsSentDate = lastSmsSentDate,
        registrationSmsError = smsError,
        registrationToken = token,
    )

    fun ParticipantDB.toModel() = Registered(id, lastname, firstname, email, telephone, registrationDate,
        registrationSmsStatus, registrationNbSmsSent, registrationLastSmsSentDate, registrationSmsError, registrationToken)
}