package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.admin.ParticipationInfosDTO
import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipationInfosPort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController("adminParticipationCtrl")
@RequestMapping("/admin/participations")
class ParticipationsCtrl (
    val participationInfosPort: ParticipationInfosPort
){
    @GetMapping("/group/{id}")
    fun getParticipationsOfGroup(@PathVariable id: UUID): List<ParticipationInfosDTO> {
        return participationInfosPort.getByGroup(id).map { it.toDto() }
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