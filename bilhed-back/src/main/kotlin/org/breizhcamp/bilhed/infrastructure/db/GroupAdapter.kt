package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonFilter
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

    override fun getByMemberId(memberId: UUID): Group {
        return groupRepo.findByMemberId(memberId).toGroup()
    }

    override fun list(): List<Group> {
        return groupRepo.findAll().map { it.toGroup() }
    }

    override fun extendedGroupList(filter: PersonFilter): Map<Group, List<Person>> {
        // even if one person is filtered, we want all of their member with the same status
        val persons = personRepo.filterPerson(filter)
        val allPeople = persons.toMutableSet()
        persons.forEach { allPeople.addAll(personRepo.findByGroupIdAndStatus(it.group.id, it.status)) }

        return allPeople
            .groupBy { it.group.toGroup() }
            .mapValues { (group, people) ->
                people.map { it.toPerson() }.sortedByDescending { it.id == group.referentId }
            }
    }

    override fun extendedGroupById(groupId: UUID): Pair<Group, List<Person>> {
        val persons = personRepo.filterPerson(PersonFilter(groupId = groupId))
        val group = persons.firstOrNull()?.group?.toGroup()
            ?: throw EntityNotFoundException("Unable to find group [$groupId]")

        val referent = persons.firstOrNull { it.id == group.referentId }
            ?: error("Referent with id ${group.referentId} not found among group members")

        val orderedPersons= listOf(referent) + persons.filter { it.id != referent.id }
        return group to orderedPersons.map { it.toPerson() }
    }

    override fun listIdsWithNoDraw(): Map<PassType, List<UUID>> {
        return groupRepo.listGroupWithNoDraw()
            .distinctBy { it.group.id to it.pass }
            .groupBy({ it.pass }, { it.group.id })
    }

    override fun updateDrawOrder(id: UUID, drawOrder: Int) {
        groupRepo.updateDrawOrder(id, drawOrder)
    }
}