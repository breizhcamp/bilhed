package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.application.dto.PersonDTO
import org.breizhcamp.bilhed.application.dto.ReferentInfosDTO

data class GroupCompleteParticipant(
    val group: GroupDTO,
    val referentInfos: ReferentInfosDTO,
    val members: List<PersonDTO>
)
