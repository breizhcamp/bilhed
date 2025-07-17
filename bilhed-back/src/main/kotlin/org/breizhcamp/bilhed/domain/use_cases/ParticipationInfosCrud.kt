package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipationInfosPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ParticipationInfosCrud (
    private val participationInfosPort: ParticipationInfosPort
) {
    fun getByGroup(id: UUID): List<ParticipationInfos> {
        return participationInfosPort.getByGroup(id)
    }
    fun getByGroups(ids: List<UUID>): List<ParticipationInfos> {
        return participationInfosPort.getByGroups(ids)
    }

    fun getByPersons(ids: List<UUID>): List<ParticipationInfos> {
        return participationInfosPort.get(ids)
    }
}