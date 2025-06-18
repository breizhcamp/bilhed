package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import java.util.UUID

interface ParticipationInfosPort {
    fun get(id: UUID): ParticipationInfos

    fun get(ids: List<UUID>): List<ParticipationInfos>

    fun save(partInfos: ParticipationInfos)

    fun list(): List<ParticipationInfos>

    fun getByGroup(id: UUID): List<ParticipationInfos>

    fun getByGroups(ids: List<UUID>): List<ParticipationInfos>

    fun existsByPersonId(id: UUID): Boolean
}