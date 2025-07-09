package org.breizhcamp.bilhed.application.rest.admin

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.dto.ErrorRes
import org.breizhcamp.bilhed.application.dto.PersonDTO
import org.breizhcamp.bilhed.application.dto.admin.UpdateContactReq
import org.breizhcamp.bilhed.domain.entities.Reminder
import org.breizhcamp.bilhed.domain.use_cases.PersonCrud
import org.breizhcamp.bilhed.domain.use_cases.Registration
import org.breizhcamp.bilhed.domain.use_cases.ReminderCrud
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController("adminPersonCtrl")
@RequestMapping("/admin/persons")
class PersonCtrl (
    private val reminderCrud: ReminderCrud,
    private val personCrud: PersonCrud,
    private val registration: Registration
) {
    @GetMapping("/{id}")
    fun getPerson(@PathVariable id: UUID): PersonDTO {
        return personCrud.get(id).toDto()
    }

    @GetMapping("/{id}/reminders")
    fun getReminders(@PathVariable id: UUID): List<Reminder> {
        return reminderCrud.listByPersonId(id)
    }

    @PutMapping("/{id}")
    fun updateContact(@PathVariable id: UUID, @RequestBody req: UpdateContactReq) {
        req.validate()
        personCrud.updateContact(id, req)
    }

    @PostMapping("/levelUp")
    fun levelUp(@RequestBody ids: List<UUID>) = ids.forEach {
        registration.levelUpAndNotify(it)
    }

    @ExceptionHandler(EntityNotFoundException::class) @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleENFE() = ErrorRes("Une erreur est survenue")

    @ExceptionHandler(IllegalArgumentException::class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIAE(e: IllegalArgumentException) = ErrorRes(e.message ?: "Une erreur est survenue")
}
