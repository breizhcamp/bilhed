package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.ReferentInfos
import org.breizhcamp.bilhed.domain.use_cases.ports.ReferentInfosPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toReferentInfos
import org.breizhcamp.bilhed.infrastructure.db.repos.ReferentInfosRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReferentInfosAdapter(
    val referentInfosRepo: ReferentInfosRepo
): ReferentInfosPort {
    override fun list(): List<ReferentInfos> {
        return referentInfosRepo.findAll().map { it.toReferentInfos() }
    }

    override fun save(infos: ReferentInfos) {
        referentInfosRepo.save(infos.toDB())
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

}