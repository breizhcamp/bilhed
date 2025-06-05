package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Group
import java.util.*

interface GroupPort {

    fun save(group: Group)

    fun isReferent(referentId: UUID): Boolean

    fun get(id: UUID): Group

    fun get(ids: List<UUID>): List<Group>

    fun list(): List<Group>

    fun getByReferentId(referentId: UUID): Group

    fun findGroups(ids: List<UUID>): List<Group>
}