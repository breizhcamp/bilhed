package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.application.dto.admin.UpdateEmailReq
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import java.util.*

interface PersonPort {

    fun list(): List<Person>

    fun filter(filter: PersonFilter): List<Person>

    fun save(person: Person)

    fun listTopDrawByPassWithLimit(pass: PassType, limit: Int): List<Person>

    fun listIdsWithNoDraw(): Map<PassType, List<UUID>>

    fun updateDrawOrder(id: UUID, drawOrder: Int)

    fun getAlreadyNotifCount(): Map<PassType, Int>

    fun updateEmail(id: UUID, updateEmailReq: UpdateEmailReq)

    fun levelUpToAttendee(id: UUID): Person

    fun levelUpToReleased(id: UUID)

    fun levelUpToParticipant(id: UUID)

    fun existsEmailOrPhone(email: String, telephone: String?): Boolean

    fun get(id: UUID): Person

    fun get(ids: List<UUID>): List<Person>

    fun getParticipant(id: UUID): Person

    fun setPayed(ids: List<UUID>)

    fun getCompanions(groupId: UUID, referentId: UUID): List<Person>

    fun getMembers(id: UUID): List<Person>

    fun getReferentOfGroup(groupId: UUID): Person

    fun listReferents(status: PersonStatus): List<Person>
}