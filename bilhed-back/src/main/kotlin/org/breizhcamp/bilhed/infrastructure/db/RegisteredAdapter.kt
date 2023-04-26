package org.breizhcamp.bilhed.infrastructure.db

import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDBStatus.*
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipantRepo
import org.springframework.stereotype.Service

@Service
class RegisteredAdapter(
    private val participantRepo: ParticipantRepo,
): RegisteredPort {

    override fun existsEmailOrPhone(email: String, phone: String): Boolean {
        return participantRepo.countByEmailOrTelephone(email, phone) > 0
    }

    override fun save(registered: Registered) {
        participantRepo.save(registered.toDB())
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
        registrationToken = token
    )

}