package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import java.util.*

interface GroupPort {

    fun save(group: Group)

    fun isReferent(referentId: UUID): Boolean

    fun get(id: UUID): Group

    fun get(ids: List<UUID>): List<Group>

    fun list(): List<Group>

    fun findGroups(ids: List<UUID>): List<Group>

    fun extendedGroupListByStatus(status: PersonStatus): Map<Group, List<Person>>

    fun extendedGroupByStatus(groupId: UUID): Pair<Group, List<Person>>
}