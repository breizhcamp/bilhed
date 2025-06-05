package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.PersonDTO
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.use_cases.ParticipantConfirm
import org.breizhcamp.bilhed.domain.use_cases.ParticipantDraw
import org.breizhcamp.bilhed.domain.use_cases.ParticipantNotif
import org.breizhcamp.bilhed.domain.use_cases.PersonCrud
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController("adminParticipantCtrl")
@RequestMapping("/admin/participants")
class ParticipantCtrl(
    private val participantDraw: ParticipantDraw,
    private val participantNotif: ParticipantNotif,
    private val participantConfirm: ParticipantConfirm,
    private val personCrud: PersonCrud
) {

    @GetMapping
    fun listParticipants(): List<PersonDTO> = personCrud.filter(PersonFilter(status = PersonStatus.PARTICIPANT)).map { it.toDto() }

    @PostMapping("/levelUp/attendee")
    fun levelUpToAttendee(@RequestBody ids: List<UUID>): Int = participantConfirm.confirmList(ids).size

    @PostMapping("/levelUp/release") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun levelUpToRelease(@RequestBody ids: List<UUID>) = participantConfirm.release(ids)

    @PostMapping("/filter")
    fun filter(@RequestBody filter: PersonFilter): List<PersonDTO> = personCrud.filter(filter).map { it.toDto() }

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
