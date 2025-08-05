package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.RegistrationInfosDTO
import org.breizhcamp.bilhed.application.dto.admin.GroupCompleteAttendee
import org.breizhcamp.bilhed.application.dto.admin.GroupCompleteParticipant
import org.breizhcamp.bilhed.application.dto.admin.GroupDTO
import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.entities.RegistrationInfos
import org.breizhcamp.bilhed.domain.use_cases.GroupCrud
import org.breizhcamp.bilhed.domain.use_cases.GroupDraw
import org.breizhcamp.bilhed.domain.use_cases.ParticipationInfosCrud
import org.breizhcamp.bilhed.domain.use_cases.RegistrationInfosCrud
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/admin/groups")
class GroupCtrl(
    val groupCrud: GroupCrud,
    val registrationInfosCrud: RegistrationInfosCrud,
    val groupDraw: GroupDraw,
    val participationInfosCrud: ParticipationInfosCrud
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
    fun getCompleteGroup(@PathVariable id: UUID): GroupCompleteParticipant {
        val groupEntry = groupCrud.extendedGroupBy(id)
        val ref = groupEntry.second.find { it.id == groupEntry.first.referentId } ?: throw IllegalStateException("Group [$id] has no referent.")

        return GroupCompleteParticipant(
            group = groupEntry.first.toDto(),
            registrationInfos = registrationInfosCrud.get(ref.id).toDto(),
            members = groupEntry.second.map { it.toDto() }
        )
    }

    @PostMapping("/participant/complete")
    fun getCompleteGroupParticipantList(@RequestBody filter: PersonFilter): List<GroupCompleteParticipant> {
        /**
         * Get all groups completes with status participant, include referent infos
         */
        val groupEntries = groupCrud.extendedGroupList(filter)

        val referentMap = groupEntries.mapNotNull { (group, people) ->
            people.find { it.id == group.referentId }?.let { group.referentId to it }
        }.toMap()

        val regInfosMap = registrationInfosCrud.get(referentMap.keys.toList())
            .associateBy { it.personId }

        return groupEntries.map { (group, members) ->
            // it may be that the referent has already paid for his place but not the others if payment is separate
            // so we need to retrieve the ref info from the referentId
            val registrationInfos = regInfosMap[group.referentId] ?: registrationInfosCrud.get(group.referentId)

            GroupCompleteParticipant(
                group = group.toDto(),
                registrationInfos = registrationInfos.toDto(),
                members = members.map { it.toDto() }
            )
        }
    }

    @PostMapping("/attendee/complete")
        /**
         * Get all groups completes with status attendee
         * If group payment, referent is the only member to have participationInfos
         * Otherwise we don't care about referent
         */
    fun getCompleteGroupAttendeeList(@RequestBody filter: PersonFilter): List<GroupCompleteAttendee> {
        val groupEntries = groupCrud.extendedGroupList(filter)

        val partInfosMap = participationInfosCrud.getByGroups(groupEntries.keys.map { it.id })
            .associateBy { it.personId }

        return groupEntries.map { (group, members) ->

            val partInfosList = mutableListOf<ParticipationInfos>()
            if (group.groupPayment) {
                val ref = members.find { it.id == group.referentId } ?: error("Referent not found for group ${group.id}")
                partInfosList.add(partInfosMap.getValue(ref.id))
            } else {
                members.forEach { partInfosList.add(partInfosMap.getValue(it.id)) }
            }

            GroupCompleteAttendee(
                group = group.toDto(),
                participationInfos = partInfosList.map { it.toDto() },
                members = members.map { it.toDto() }
            )
        }
    }

    @PostMapping("/draw") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun draw() {
        groupDraw.draw()
    }
}

fun Group.toDto() = GroupDTO(
    id = this.id,
    referentId = this.referentId,
    pass = this.pass,
    groupPayment = this.groupPayment,
    drawOrder = this.drawOrder,
)

fun RegistrationInfos.toDto() = RegistrationInfosDTO(
    personId = personId,
    registrationDate = registrationDate,
    smsStatus = smsStatus,
    nbSmsSent = nbSmsSent,
    lastSmsSentDate = lastSmsSentDate,
    smsError = smsError,
    token = token,
    nbTokenTries = nbTokenTries
)