package org.breizhcamp.bilhed.application.rest.admin

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.dto.ErrorRes
import org.breizhcamp.bilhed.application.dto.PersonDTO
import org.breizhcamp.bilhed.application.dto.admin.NotificationDTO
import org.breizhcamp.bilhed.application.dto.admin.PersonCompleteDTO
import org.breizhcamp.bilhed.application.dto.admin.UpdateContactReq
import org.breizhcamp.bilhed.domain.entities.Notification
import org.breizhcamp.bilhed.domain.use_cases.NotificationCrud
import org.breizhcamp.bilhed.domain.use_cases.PersonCrud
import org.breizhcamp.bilhed.domain.use_cases.Registration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController("adminPersonCtrl")
@RequestMapping("/admin/persons")
class PersonCtrl (
    private val notificationCrud: NotificationCrud,
    private val personCrud: PersonCrud,
    private val registration: Registration
) {
    @GetMapping("/{id}")
    fun getPerson(@PathVariable id: UUID): PersonDTO {
        return personCrud.get(id).toDto()
    }

    @GetMapping("/{id}/complete")
    fun getPersonComplete(@PathVariable id: UUID): PersonCompleteDTO {
        val personComplete = personCrud.getPersonComplete(id = id)
        return PersonCompleteDTO(
            person = personComplete.person.toDto(),
            group = personComplete.group.toDto(),
            attendeeData = personComplete.attendeeData?.toDto(),
            notifs = personComplete.notifs.map { it.toDto() },
        )
    }

    @GetMapping("/{id}/reminders")
    fun getReminders(@PathVariable id: UUID): List<NotificationDTO> {
        return notificationCrud.listByPersonId(id).map { it.toDto() }
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

fun Notification.toDto() = NotificationDTO (
    id = this.id,
    reminderDate = this.reminderDate,
    template = this.template,
    method = this.method,
    personId = this.personId,
    model = this.model,
    origin = this.origin,
)
