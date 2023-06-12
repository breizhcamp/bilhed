package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.admin.AttendeeDTO
import org.breizhcamp.bilhed.application.dto.admin.ParticipantDTO
import org.breizhcamp.bilhed.domain.entities.Attendee
import org.breizhcamp.bilhed.domain.entities.AttendeeFilter
import org.breizhcamp.bilhed.domain.entities.ParticipantFilter
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.use_cases.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController("adminAttendeeCtrl")
@RequestMapping("/admin/attendees")
class AttendeeCtrl(
    private val attendeeList: AttendeeList,
    private val attendeeNotify: AttendeeNotify,
) {

    @GetMapping
    fun list(): List<AttendeeDTO> = attendeeList.filter(AttendeeFilter.empty()).map { it.toDto() }

    @PostMapping("/filter")
    fun filter(@RequestBody filter: AttendeeFilter): List<AttendeeDTO> = attendeeList.filter(filter).map { it.toDto() }

    @PostMapping("/notif/payed/reminder/mail") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun payedReminderMail(@RequestBody ids: List<UUID>) {
        attendeeNotify.remindPayedMail(ids)
    }

    @PostMapping("/notif/payed/reminder/sms") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun payedReminderSms(@RequestBody ids: List<UUID>) {
        attendeeNotify.remindPayedSms(ids)
    }
}

private fun Attendee.toDto() = AttendeeDTO(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,

    participantConfirmationDate = participantConfirmationDate,
    payed = payed,
)

