package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import java.util.*

interface GroupPort {

    fun save(group: Group)

    fun isReferent(referentId: UUID): Boolean

    fun get(id: UUID): Group

    fun get(ids: List<UUID>): List<Group>

    fun list(): List<Group>

    fun extendedGroupList(filter: PersonFilter): Map<Group, List<Person>>

    fun extendedGroupById(groupId: UUID): Pair<Group, List<Person>>

    fun listIdsWithNoDraw(): Map<PassType, List<UUID>>

    fun updateDrawOrder(id: UUID, drawOrder: Int)
}