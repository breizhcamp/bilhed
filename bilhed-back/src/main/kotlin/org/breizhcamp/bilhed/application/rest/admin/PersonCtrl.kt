package org.breizhcamp.bilhed.application.rest.admin

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.dto.ErrorRes
import org.breizhcamp.bilhed.application.dto.admin.PersonDTO
import org.breizhcamp.bilhed.application.dto.admin.UpdateContactReq
import org.breizhcamp.bilhed.domain.entities.Reminder
import org.breizhcamp.bilhed.domain.use_cases.PersonDetail
import org.breizhcamp.bilhed.domain.use_cases.ReminderCrud
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController("adminPersonCtrl")
@RequestMapping("/admin/person")
class PersonCtrl (
    private val personDetail: PersonDetail,
    private val reminderCrud: ReminderCrud
) {
    @GetMapping("/{id}")
    fun getPerson(@PathVariable id: UUID): PersonDTO {
        return personDetail.get(id)
    }

    @GetMapping("/{id}/reminders")
    fun getReminders(@PathVariable id: UUID): List<Reminder> {
        return reminderCrud.listByPersonId(id)
    }

    @PutMapping("/{id}")
    fun updateEmailPhone(@PathVariable id: UUID, @RequestBody req: UpdateContactReq) {
        req.validate()
        personDetail.updateContact(id, req)
    }

    @ExceptionHandler(EntityNotFoundException::class) @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleENFE(e: EntityNotFoundException) = ErrorRes("Une erreur est survenue")

    @ExceptionHandler(IllegalArgumentException::class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIAE(e: IllegalArgumentException) = ErrorRes(e.message ?: "Une erreur est survenue")
}
