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
import org.breizhcamp.bilhed.domain.use_cases.ReferentInfosCrud
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.Locale.getDefault

@RestController
@RequestMapping("/admin/groups")
class GroupCtrl(
    val groupCrud: GroupCrud,
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

    @GetMapping("/{id}/complete")
    fun getCompleteGroup(@PathVariable id: UUID): GroupComplete {
        val groupEntry = groupCrud.extendedGroupById(id)
        val ref = groupEntry.second.find { it.id == groupEntry.first.referentId } ?: throw IllegalStateException("Group [$id] has no referent.")

        return GroupComplete(
            group = groupEntry.first.toDto(),
            referent = ReferentDTO(ref.toDto(), referentInfosCrud.get(ref.id).toDto()),
            companions = groupEntry.second.filter { it.id != groupEntry.first.referentId }.map { it.toDto() }
        )
    }

    @PostMapping("/complete/status")
    fun getCompleteGroupByStatus(@RequestBody req: StatusReq): List<GroupComplete> {
        val status = PersonStatus.valueOf(req.status.trim().uppercase(getDefault()))

        val groupEntries = groupCrud.extendedGroupListByStatus(status)

        val referentMap = groupEntries.mapNotNull { (group, people) ->
            people.find { it.id == group.referentId }?.let { group.referentId to it }
        }.toMap()

        val refInfosMap = referentInfosCrud.get(referentMap.keys.toList())
            .associateBy { it.personId }

        return groupEntries.map { (group, members) ->
            val referent = referentMap[group.referentId]
                ?: error("Referent not found for group ${group.id}")
            val referentInfos = refInfosMap[referent.id]
                ?: error("Referent infos not found for person ${referent.id}")

            GroupComplete(
                group = group.toDto(),
                referent = ReferentDTO(
                    referent.toDto(),
                    referentInfos.toDto()
                ),
                companions = members.filter { it.id != group.referentId }.map { it.toDto() }
            )
        }
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