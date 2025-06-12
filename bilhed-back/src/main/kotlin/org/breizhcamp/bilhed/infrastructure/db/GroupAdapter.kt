package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.GroupPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toGroup
import org.breizhcamp.bilhed.infrastructure.db.mappers.toPerson
import org.breizhcamp.bilhed.infrastructure.db.repos.GroupRepo
import org.breizhcamp.bilhed.infrastructure.db.repos.PersonRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class GroupAdapter (
    val groupRepo: GroupRepo,
    val personRepo: PersonRepo,
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

    override fun findGroups(ids: List<UUID>): List<Group> {
        return groupRepo.findGroups(ids).map { it.toGroup() }
    }

    override fun extendedGroupListByStatus(status: PersonStatus): Map<Group, List<Person>> {
        val persons = personRepo.filterPerson(PersonFilter(status = status))

        return persons
            .groupBy { it.group.toGroup() }
            .mapValues { (_, people) -> people.map { it.toPerson() } }
    }

    override fun extendedGroupByStatus(groupId: UUID): Pair<Group, List<Person>> {
        val persons = personRepo.filterPerson(PersonFilter(groupId = groupId))
        val group = persons.firstOrNull()?.group?.toGroup()
            ?: throw EntityNotFoundException("Unable to find group [$groupId]")

        return group to persons.map { it.toPerson() }
    }
}