package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.admin.ParticipationInfosDTO
import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.domain.use_cases.ParticipationInfosCrud
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController("adminParticipationCtrl")
@RequestMapping("/admin/participations")
class ParticipationsCtrl (
    val participationInfosCrud: ParticipationInfosCrud
){
    @GetMapping("/group/{id}")
    fun getParticipationsOfGroup(@PathVariable id: UUID): List<ParticipationInfosDTO> {
        return participationInfosCrud.getByGroup(id).map { it.toDto() }
    }

    @PostMapping("/groups")
    fun getParticipationsInfosByGroups(@RequestBody ids: List<UUID>): List<ParticipationInfosDTO> {
        return participationInfosCrud.getByGroups(ids).map { it.toDto() }
    }

    @PostMapping("/persons")
    fun getParticipationsInfosByPersons(@RequestBody personIds: List<UUID>): List<ParticipationInfosDTO> {
        return participationInfosCrud.getByPersons(personIds).map { it.toDto() }
    }
}

fun ParticipationInfos.toDto() = ParticipationInfosDTO(
    personId = personId,
    smsStatus = smsStatus,
    nbSmsSent = nbSmsSent,
    smsError = smsError,
    notificationConfirmSentDate = notificationConfirmSentDate,
    confirmationDate = confirmationDate
)