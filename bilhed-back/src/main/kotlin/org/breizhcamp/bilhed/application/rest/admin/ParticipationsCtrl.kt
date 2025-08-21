package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.admin.ParticipationInfosDTO
import org.breizhcamp.bilhed.domain.entities.ParticipationInfo
import org.breizhcamp.bilhed.domain.use_cases.ParticipationInfoCrud
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController("adminParticipationCtrl")
@RequestMapping("/admin/participations")
class ParticipationsCtrl (
    val participationInfoCrud: ParticipationInfoCrud
){
    @GetMapping("/group/{id}")
    fun getParticipationsOfGroup(@PathVariable id: UUID): List<ParticipationInfosDTO> {
        return participationInfoCrud.getByGroup(id).map { it.toDto() }
    }

    @PostMapping("/groups")
    fun getParticipationsInfosByGroups(@RequestBody ids: List<UUID>): List<ParticipationInfosDTO> {
        return participationInfoCrud.getByGroups(ids).map { it.toDto() }
    }

    @PostMapping("/persons")
    fun getParticipationsInfosByPersons(@RequestBody personIds: List<UUID>): List<ParticipationInfosDTO> {
        return participationInfoCrud.getByPersons(personIds).map { it.toDto() }
    }
}

fun ParticipationInfo.toDto() = ParticipationInfosDTO(
    personId = personId,
    smsStatus = smsStatus,
    nbSmsSent = nbSmsSent,
    smsError = smsError,
    notificationConfirmSentDate = notificationConfirmSentDate,
    confirmationDate = confirmationDate,
    payed = payed
)