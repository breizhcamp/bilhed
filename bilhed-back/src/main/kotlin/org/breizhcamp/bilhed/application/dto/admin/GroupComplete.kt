package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.application.dto.PersonDTO
import org.breizhcamp.bilhed.application.dto.ReferentDTO

data class GroupComplete(
    val group: GroupDTO,
    val referent: ReferentDTO,
    val companions: List<PersonDTO>
)
