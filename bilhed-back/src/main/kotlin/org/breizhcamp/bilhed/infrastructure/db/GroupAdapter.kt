package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.use_cases.ports.GroupPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toGroup
import org.breizhcamp.bilhed.infrastructure.db.repos.GroupRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class GroupAdapter (
    val groupRepo: GroupRepo,
): GroupPort {
    override fun save(group: Group) {
        groupRepo.save(group.toDB())
    }

    override fun isReferent(referentId: UUID): Boolean {
        return groupRepo.countByReferentId(referentId) > 0
    }

    override fun get(id: UUID): Group {
        return groupRepo.findByIdOrNull(id)?.toGroup() ?: throw EntityNotFoundException("Unable to find group [$id]")
    }

    override fun get(ids: List<UUID>): List<Group> {
        return groupRepo.findAllById(ids).map { it.toGroup() }
    }

    override fun list(): List<Group> {
        return groupRepo.findAll().map { it.toGroup() }
    }

    override fun getByReferentId(referentId: UUID): Group {
        return groupRepo.findByReferentId(referentId)?.toGroup() ?: throw EntityNotFoundException("Unable to find group with referent [$referentId]")
    }

    override fun findGroups(ids: List<UUID>): List<Group> {
        return groupRepo.findGroups(ids).map { it.toGroup() }
    }
}