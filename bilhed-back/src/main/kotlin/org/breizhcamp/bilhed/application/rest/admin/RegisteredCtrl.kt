package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.admin.RegisteredDTO
import org.breizhcamp.bilhed.application.dto.admin.ReminderReq
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.use_cases.RegisteredImport
import org.breizhcamp.bilhed.domain.use_cases.RegisteredList
import org.breizhcamp.bilhed.domain.use_cases.RegisteredReminder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/admin/registered")
class RegisteredCtrl(
    private val registeredList: RegisteredList,
    private val registeredImport: RegisteredImport,
    private val registeredReminder: RegisteredReminder,
) {

    @GetMapping
    fun listRegistered(): List<RegisteredDTO> = registeredList.list().map { it.toDTO() }

    @PostMapping("/import")
    fun importCsv(file: MultipartFile) {
        registeredImport.importCsv(file.inputStream)
    }

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

fun Registered.toDTO() = RegisteredDTO(
    id = id,

    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,

    registrationDate = registrationDate,

    smsStatus = smsStatus,
    nbSmsSent = nbSmsSent,
    lastSmsSentDate = lastSmsSentDate,
    smsError = smsError,
    token = token,
    nbTokenTries = nbTokenTries,
)