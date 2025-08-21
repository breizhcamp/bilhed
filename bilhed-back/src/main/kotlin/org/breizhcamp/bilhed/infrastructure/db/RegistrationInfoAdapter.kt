package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.RegistrationInfo
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.RegistrationInfoPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toRegistrationInfos
import org.breizhcamp.bilhed.infrastructure.db.repos.PersonRepo
import org.breizhcamp.bilhed.infrastructure.db.repos.RegistrationInfosRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.util.*

@Component
class RegistrationInfoAdapter(
    val registrationInfosRepo: RegistrationInfosRepo,
    val personRepo: PersonRepo
): RegistrationInfoPort {
    override fun list(status: PersonStatus): List<RegistrationInfo> {
        return registrationInfosRepo.findAllByStatus(status.toDB()).map { it.toRegistrationInfos() }
    }

    override fun save(infos: RegistrationInfo) {
        registrationInfosRepo.save(infos.toDB(personRepo.getReferenceById(infos.personId)))
    }

    override fun get(id: UUID): RegistrationInfo {
        return registrationInfosRepo.findByIdOrNull(id)?.toRegistrationInfos() ?: throw EntityNotFoundException()
    }

    override fun resetSmsCount(id: UUID) {
        registrationInfosRepo.resetSmsCount(id)
    }

    override fun get(ids: List<UUID>): List<RegistrationInfo> {
        return registrationInfosRepo.findAllById(ids).map { it.toRegistrationInfos() }
    }

    override fun updateSms(
        id: UUID,
        smsStatus: SmsStatus,
        error: String?,
        nbSmsSent: Int?,
        lastSmsSentDate: ZonedDateTime?
    ) {
        val regInfos = registrationInfosRepo.findByIdOrNull(id) ?: throw EntityNotFoundException("Referent Infos with id [$id] not found.")
        regInfos.apply {
            registrationSmsStatus = smsStatus
            registrationSmsError = error
            if (nbSmsSent != null) registrationNbSmsSent = nbSmsSent
            if (lastSmsSentDate != null) registrationLastSmsSentDate = lastSmsSentDate
        }
    }

}