package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.application.dto.admin.UpdateContactReq
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import java.util.*

interface PersonPort {

    fun filter(filter: PersonFilter): List<Person>

    fun save(person: Person)

    fun getAlreadyNotifCount(): Map<PassType, Int>

    fun updateContact(id: UUID, updateContactReq: UpdateContactReq)

    fun levelUpTo(id: UUID, newStatus: PersonStatus): Person

    fun existsEmailOrPhone(email: String, telephone: String?): Boolean

    fun get(id: UUID): Person

    fun get(ids: List<UUID>): List<Person>

    fun setPayed(ids: List<UUID>)

    fun getCompanions(groupId: UUID, referentId: UUID): List<Person>

    fun getMembersByGroup(id: UUID): List<Person>

    fun getReferentOfGroup(groupId: UUID): Person
}