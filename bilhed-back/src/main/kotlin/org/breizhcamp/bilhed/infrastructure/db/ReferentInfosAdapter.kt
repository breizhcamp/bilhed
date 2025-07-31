package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.ReferentInfos
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.ReferentInfosPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toReferentInfos
import org.breizhcamp.bilhed.infrastructure.db.repos.PersonRepo
import org.breizhcamp.bilhed.infrastructure.db.repos.ReferentInfosRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.util.*

@Component
class ReferentInfosAdapter(
    val referentInfosRepo: ReferentInfosRepo,
    val personRepo: PersonRepo
): ReferentInfosPort {
    override fun list(status: PersonStatus): List<ReferentInfos> {
        return referentInfosRepo.findAllByStatus(status.toDB()).map { it.toReferentInfos() }
    }

    override fun save(infos: ReferentInfos) {
        referentInfosRepo.save(infos.toDB(personRepo.getReferenceById(infos.personId)))
    }

    override fun get(id: UUID): ReferentInfos {
        return referentInfosRepo.findByIdOrNull(id)?.toReferentInfos() ?: throw EntityNotFoundException()
    }

    override fun resetSmsCount(id: UUID) {
        referentInfosRepo.resetSmsCount(id)
    }

    override fun get(ids: List<UUID>): List<ReferentInfos> {
        return referentInfosRepo.findAllById(ids).map { it.toReferentInfos() }
    }

    override fun updateSms(
        id: UUID,
        smsStatus: SmsStatus,
        error: String?,
        nbSmsSent: Int?,
        lastSmsSentDate: ZonedDateTime?
    ) {
        val refInfos = referentInfosRepo.findByIdOrNull(id) ?: throw EntityNotFoundException("Referent Infos with id [$id] not found.")
        refInfos.apply {
            registrationSmsStatus = smsStatus
            registrationSmsError = error
            if (nbSmsSent != null) registrationNbSmsSent = nbSmsSent
            if (lastSmsSentDate != null) registrationLastSmsSentDate = lastSmsSentDate
        }
    }

}