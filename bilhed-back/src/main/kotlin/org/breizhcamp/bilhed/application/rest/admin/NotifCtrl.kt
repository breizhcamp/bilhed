package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.admin.ReminderReq
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.use_cases.ParticipantConfirm
import org.breizhcamp.bilhed.domain.use_cases.ParticipantNotif
import org.breizhcamp.bilhed.domain.use_cases.RegisteredReminder
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController("adminNotifCtrl")
@RequestMapping("/admin/notifs")
class NotifCtrl(
    private val participantNotif: ParticipantNotif,
    private val participantConfirm: ParticipantConfirm,
    private val registeredReminder: RegisteredReminder,
) {

    @PostMapping("/levelUp/attendee")
    fun levelUpToAttendee(@RequestBody ids: List<UUID>): Int = participantConfirm.confirmList(ids).size

    @PostMapping("/levelUp/release") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun levelUpToRelease(@RequestBody ids: List<UUID>) = participantConfirm.release(ids)

    @PostMapping("/success") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun succeed(@RequestBody ids: List<UUID>) {
        participantNotif.notifySuccess(ids)
    }

    @PostMapping("/success/reminder") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun successReminder(@RequestBody ids: List<UUID>) {
        participantNotif.remindSuccess(ids, ReminderOrigin.MANUAL)
    }

    @PostMapping("/waiting") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun waiting(@RequestBody ids: List<UUID>) {
        participantNotif.notifyWaiting(ids) // TODO : Pq pas appeler la bonne méthode directement ?
    }

    @PostMapping("/failed") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun failed(@RequestBody ids: List<UUID>) {
        participantNotif.notifyFailed(ids)
    }

    @PostMapping("/registered/{id}/reminder")
    fun sendReminder(@PathVariable id: UUID, @RequestBody req: ReminderReq) {
        val smsTemplate = if (req.sms != null && req.sms) "registration_reminder" else ""
        val emailTemplate = if (req.email != null && req.email) "registration_reminder" else ""
        registeredReminder.send(id, smsTemplate , emailTemplate, ReminderOrigin.MANUAL)
    }
}
