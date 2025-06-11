package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.ReferentDTO
import org.breizhcamp.bilhed.application.dto.ReferentInfosDTO
import org.breizhcamp.bilhed.application.dto.admin.GroupComplete
import org.breizhcamp.bilhed.application.dto.admin.GroupDTO
import org.breizhcamp.bilhed.application.dto.admin.StatusReq
import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.ReferentInfos
import org.breizhcamp.bilhed.domain.use_cases.GroupCrud
import org.breizhcamp.bilhed.domain.use_cases.GroupStatus
import org.breizhcamp.bilhed.domain.use_cases.PersonCrud
import org.breizhcamp.bilhed.domain.use_cases.ReferentInfosCrud
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.Locale.getDefault

@RestController
@RequestMapping("/admin/groups")
class GroupCtrl(
    val groupCrud: GroupCrud,
    val personCrud: PersonCrud,
    val referentInfosCrud: ReferentInfosCrud,
    val groupStatus: GroupStatus,
) {

    @GetMapping
    fun listGroup(): List<GroupDTO> {
        return groupCrud.list().map { it.toDto() }
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): GroupDTO {
        return groupCrud.get(id).toDto()
    }

    @GetMapping("/complete")
    fun listGroupComplete(): List<GroupComplete> {
        return groupCrud.list().map { GroupComplete(
            it.toDto(),
            ReferentDTO(
                personCrud.get(it.referentId).toDto(),
                referentInfosCrud.get(it.referentId).toDto(),

                ),
            personCrud.getCompanions(it.id, it.referentId).map { pers -> pers.toDto() },
        ) }
    }

    @GetMapping("/{id}/complete")
    fun getCompleteGroup(@PathVariable id: UUID): GroupComplete {
        return groupCrud.get(id).let { GroupComplete(
            it.toDto(),
            ReferentDTO (
                personCrud.get(it.referentId).toDto(),
                referentInfosCrud.get(it.referentId).toDto(),
            ),
            personCrud.getCompanions(it.id, it.referentId).map { pers -> pers.toDto() },
        )}
    }

    @PostMapping("/complete/status")
    fun getCompleteGroupByStatus(@RequestBody req: StatusReq): List<GroupComplete> {
        val status = PersonStatus.valueOf(req.status.trim().uppercase(getDefault()))

        val referents = personCrud.listReferents(status)
        val groups = groupCrud.getGroups(referents.map { it.groupId })

        return groups.map { gr -> GroupComplete(
            gr.toDto(),
            ReferentDTO(
                referents.first { it.id == gr.referentId }.toDto(),
                referentInfosCrud.get(gr.referentId).toDto(),
            ),
            personCrud.getCompanions(gr.id, gr.referentId).map { pers -> pers.toDto() },
        ) }

    }

    @PostMapping("/levelUp")
    fun levelUp(@RequestBody ids: List<UUID>) {
        groupStatus.levelUp(ids)
    }
}

fun Group.toDto() = GroupDTO(
    id = this.id,
    referentId = this.referentId,
    groupPayment = this.groupPayment,
    drawOrder = this.drawOrder,
)

fun ReferentInfos.toDto() = ReferentInfosDTO(
    personId = personId,
    registrationDate = registrationDate,
    smsStatus = smsStatus,
    nbSmsSent = nbSmsSent,
    lastSmsSentDate = lastSmsSentDate,
    smsError = smsError,
    token = token,
    nbTokenTries = nbTokenTries
)