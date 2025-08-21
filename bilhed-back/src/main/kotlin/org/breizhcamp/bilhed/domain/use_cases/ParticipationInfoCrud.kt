package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.ParticipationInfo
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipationInfoPort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class ParticipationInfoCrud (
    private val participationInfoPort: ParticipationInfoPort,
    private val personPort: PersonPort,
) {
    fun getByGroup(id: UUID): List<ParticipationInfo> {
        return participationInfoPort.getByGroup(id)
    }
    fun getByGroups(ids: List<UUID>): List<ParticipationInfo> {
        return participationInfoPort.getByGroups(ids)
    }

    fun getByPersons(ids: List<UUID>): List<ParticipationInfo> {
        return participationInfoPort.get(ids)
    }

    fun getBy(groupIds: List<UUID>, payed: Boolean?): Map<UUID, List<ParticipationInfo>> {
        var partInfo = getByGroups(ids = groupIds)
        if (payed != null) // on supprime garde ceux suivant le filtre 'payé'
            partInfo = partInfo.filter { it.payed == payed }

        // on récupère les personnes à partir des partInfo et on supprime les RELEASED
        val persons = personPort.get(partInfo.map { it.personId }).filter { it.status != PersonStatus.RELEASED }

        // On filtre les partInfo pour supprimer ceux des RELEASED et avoir persons et partInfos de même taille
        partInfo = partInfo.filter { it.personId in persons.map { p -> p.id } }

        val personsToGroup = persons.associate { it.id to it.groupId }
        return partInfo.groupBy { personsToGroup[it.personId]!! }
    }
}