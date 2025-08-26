package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.application.dto.PersonDTO
import org.breizhcamp.bilhed.application.dto.RegistrationInfosDTO

data class GroupCompleteParticipant(
    val group: GroupDTO,
    val registrationInfos: RegistrationInfosDTO,
    val members: List<PersonDTO>
)
