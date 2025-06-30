package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.application.dto.PersonDTO

data class GroupCompleteAttendee(
    val group: GroupDTO,
    val members: List<PersonDTO>,
    val participationInfos: List<ParticipationInfosDTO>
)
