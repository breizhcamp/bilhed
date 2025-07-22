package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.use_cases.ports.GroupPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class GroupCrud(
    private val groupPort: GroupPort,
) {

    fun list(): List<Group> = groupPort.list()

    fun get(id: UUID): Group = groupPort.get(id)

    fun extendedGroupList(filter: PersonFilter): Map<Group, List<Person>> = groupPort.extendedGroupList(filter)

    fun extendedGroupBy(groupId: UUID): Pair<Group, List<Person>> = groupPort.extendedGroupBy(groupId)
}