package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.dto.admin.UpdateEmailReq
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toPerson
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDBStatus
import org.breizhcamp.bilhed.infrastructure.db.repos.GroupRepo
import org.breizhcamp.bilhed.infrastructure.db.repos.PersonRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class PersonAdapter(
    private val personRepo: PersonRepo,
    private val groupRepo: GroupRepo,
): PersonPort {
    override fun list(): List<Person> {
        TODO("Not yet implemented")
    }

    override fun filter(filter: PersonFilter): List<Person> {
        return personRepo.filterPerson(filter).map{ it.toPerson() }
    }

    override fun save(person: Person) {
        personRepo.save(person.toDB(groupRepo.getReferenceById(person.groupId)))
    }

    override fun listTopDrawByPassWithLimit(
        pass: PassType,
        limit: Int
    ): List<Person> {
        TODO("Not yet implemented")
    }

    override fun listIdsWithNoDraw(): Map<PassType, List<UUID>> {
        TODO("Not yet implemented")
    }

    override fun updateDrawOrder(id: UUID, drawOrder: Int) {
        TODO("Not yet implemented")
    }

    override fun getAlreadyNotifCount(): Map<PassType, Int> {
        val res = personRepo.countAlreadyNotif().toMap()
        return PassType.values().associateWith { res[it] ?: 0 }
    }

    @Transactional
    override fun updateEmail(id: UUID, updateEmailReq: UpdateEmailReq) {
        personRepo.findByIdOrNull(id) ?: throw EntityNotFoundException("Unable to find person [$id]")
        personRepo.updateEmail(id, updateEmailReq.email)
    }

    override fun levelUpToAttendee(id: UUID): Person {
        TODO("Not yet implemented")
    }

    override fun levelUpToReleased(id: UUID) {
        personRepo.findParticipant(id)?.apply {
            status = PersonDBStatus.RELEASED
        } ?: throw EntityNotFoundException("Unable to find participant [$id]")
    }

    override fun levelUpToParticipant(id: UUID) {
        personRepo.levelUpToParticipant(id)
    }

    override fun existsEmailOrPhone(email: String, telephone: String?): Boolean {
        if (telephone != null) return personRepo.countByEmailOrTelephone(email, telephone) > 0
        return personRepo.countByEmail(email) > 0
    }

    override fun get(id: UUID): Person {
        return personRepo.findByIdOrNull(id)?.toPerson() ?: throw EntityNotFoundException()
    }

    override fun get(ids: List<UUID>): List<Person> {
        return personRepo.findAllById(ids).map { it.toPerson() }
    }

    override fun getParticipant(id: UUID): Person {
        return personRepo.findParticipant(id)?.toPerson() ?: throw EntityNotFoundException("Unable to find participant [$id]")
    }

    override fun setPayed(ids: List<UUID>) {
        return personRepo.setPayed(ids)
    }

    override fun getCompanions(groupId: UUID, referentId: UUID): List<Person> {
        return personRepo.getCompanions(groupId, referentId).map { it.toPerson() }
    }

    override fun getMembersByGroup(id: UUID): List<Person> {
        return personRepo.findByGroupId(id).map { it.toPerson() }
    }

    override fun getReferentOfGroup(groupId: UUID): Person {
        return personRepo.findReferentOfGroup(groupId).toPerson()
    }

}