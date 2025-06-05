package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.PersonDTO
import org.breizhcamp.bilhed.application.dto.admin.ReminderReq
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.use_cases.PersonCrud
import org.breizhcamp.bilhed.domain.use_cases.RegisteredImport
import org.breizhcamp.bilhed.domain.use_cases.RegisteredReminder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/admin/registered")
class RegisteredCtrl(
    private val personCrud: PersonCrud,
    private val registeredImport: RegisteredImport,
    private val registeredReminder: RegisteredReminder,
) {

    @GetMapping
    fun listRegistered(): List<PersonDTO> = personCrud.filter(PersonFilter(status = PersonStatus.REGISTERED)).map { it.toDto() }

    @PostMapping("/levelUp")
    fun levelUp(@RequestBody ids: List<UUID>) {
        registeredImport.levelUp(ids)
    }

    @PostMapping("/{id}/reminder")
    fun sendReminder(@PathVariable id: UUID, @RequestBody req: ReminderReq) {
        val smsTemplate = if (req.sms != null && req.sms) "registration_reminder" else ""
        val emailTemplate = if (req.email != null && req.email) "registration_reminder" else ""
        registeredReminder.send(id, smsTemplate , emailTemplate, ReminderOrigin.MANUAL)
    }
}