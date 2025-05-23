package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.admin.ParticipantDTO
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.ParticipantFilter
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.use_cases.ParticipantConfirm
import org.breizhcamp.bilhed.domain.use_cases.ParticipantDraw
import org.breizhcamp.bilhed.domain.use_cases.ParticipantList
import org.breizhcamp.bilhed.domain.use_cases.ParticipantNotif
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController("adminParticipantCtrl")
@RequestMapping("/admin/participants")
class ParticipantCtrl(
    private val participantList: ParticipantList,
    private val participantDraw: ParticipantDraw,
    private val participantNotif: ParticipantNotif,
    private val participantConfirm: ParticipantConfirm,
) {

    @GetMapping
    fun listParticipants(): List<ParticipantDTO> = participantList.list().map { it.toDto() }

    @PostMapping("/levelUp/attendee")
    fun levelUpToAttendee(@RequestBody ids: List<UUID>): Int = participantConfirm.confirmList(ids).size

    @PostMapping("/levelUp/release") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun levelUpToRelease(@RequestBody ids: List<UUID>) = participantConfirm.release(ids)

    @PostMapping("/filter")
    fun filter(@RequestBody filter: ParticipantFilter): List<ParticipantDTO> = participantList.filter(filter).map { it.toDto() }

    @PostMapping("/draw") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun draw() {
        participantDraw.draw()
    }

    @PostMapping("/notif") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun notifAll(@RequestBody override: Map<PassType, Int>?) {
        participantNotif.notify(override ?: emptyMap())
    }

    @PostMapping("/notif/success") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun succeed(@RequestBody ids: List<UUID>) {
        participantNotif.notifySuccess(ids)
    }

    @PostMapping("/notif/success/reminder") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun successReminder(@RequestBody ids: List<UUID>) {
        participantNotif.remindSuccess(ids, ReminderOrigin.MANUAL)
    }

    @PostMapping("/notif/waiting") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun waiting(@RequestBody ids: List<UUID>) {
        participantNotif.notifyWaiting(ids)
    }

    @PostMapping("/notif/failed") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun failed(@RequestBody ids: List<UUID>) {
        participantNotif.notifyFailed(ids)
    }
}

private fun Participant.toDto() = ParticipantDTO(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,
    participationDate = participationDate,

    drawOrder = drawOrder,
    smsStatus = smsStatus,
    nbSmsSent = nbSmsSent,
    smsError = smsError,
    notificationConfirmSentDate = notificationConfirmDate,
    confirmationDate = confirmationDate,
)
