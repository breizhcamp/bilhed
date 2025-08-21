package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipationInfoPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ParticipationInfosCrud (
    private val participationInfoPort: ParticipationInfoPort
) {
    fun getByGroup(id: UUID): List<ParticipationInfos> {
        return participationInfoPort.getByGroup(id)
    }
    fun getByGroups(ids: List<UUID>): List<ParticipationInfos> {
        return participationInfoPort.getByGroups(ids)
    }

    fun getByPersons(ids: List<UUID>): List<ParticipationInfos> {
        return participationInfoPort.get(ids)
    }
}