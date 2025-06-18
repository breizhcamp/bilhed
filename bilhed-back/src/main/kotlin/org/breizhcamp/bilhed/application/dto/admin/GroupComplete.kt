package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.application.dto.PersonDTO
import org.breizhcamp.bilhed.application.dto.ReferentInfosDTO

data class GroupComplete(
    val group: GroupDTO,
    val referentInfos: ReferentInfosDTO,
    val members: List<PersonDTO>
)
