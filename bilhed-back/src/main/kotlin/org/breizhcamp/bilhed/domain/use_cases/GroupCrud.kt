package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.use_cases.ports.GroupPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GroupCrud(
    private val groupPort: GroupPort
) {

    fun list(): List<Group> = groupPort.list()

    fun get(id: UUID): Group = groupPort.get(id)

    fun getByReferentId(referentId: UUID): Group = groupPort.getByReferentId(referentId)

    fun getGroups(ids: List<UUID>): List<Group> = groupPort.findGroups(ids)

}