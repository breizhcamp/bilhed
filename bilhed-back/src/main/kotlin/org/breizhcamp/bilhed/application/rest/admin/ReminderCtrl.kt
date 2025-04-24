package org.breizhcamp.bilhed.application.rest.admin

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.dto.ErrorRes
import org.breizhcamp.bilhed.application.dto.admin.ReminderConfigDTO
import org.breizhcamp.bilhed.application.dto.admin.ReminderConfigReq
import org.breizhcamp.bilhed.domain.entities.ReminderConfig
import org.breizhcamp.bilhed.domain.use_cases.ReminderConfigCrud
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController("reminderController")
@RequestMapping("/admin/reminders")
class ReminderCtrl (
    private val reminderConfigCrud: ReminderConfigCrud,
) {

    @GetMapping("/config")
    fun reminder(): List<ReminderConfigDTO> = reminderConfigCrud.list().map { it.toReminderConfigDTO() }

    @PostMapping("/config") @ResponseStatus(HttpStatus.CREATED)
    fun addReminder(@RequestBody reminderConfigReq: ReminderConfigReq): ReminderConfigDTO {
        reminderConfigReq.validate()
        val reminderConfigSaved: ReminderConfig = reminderConfigCrud.add(reminderConfigReq.toReminderConfig())
        return reminderConfigSaved.toReminderConfigDTO()
    }

    @PostMapping("/config/list") @ResponseStatus(HttpStatus.CREATED)
    fun addReminders(@RequestBody reminderConfigReqs: List<ReminderConfigReq>) {
        for (reminderConfig in reminderConfigReqs) {
            reminderConfig.validate()
            reminderConfigCrud.add(reminderConfig.toReminderConfig())
        }
    }

    @PutMapping("/config/{id}")
    fun updateReminder(@PathVariable id: UUID, @RequestBody reminderConfigReq: ReminderConfigReq): ReminderConfigDTO {
        reminderConfigReq.validate()
        val reminderConfigSaved = reminderConfigCrud.update(reminderConfigReq.toReminderConfig(id))
        return reminderConfigSaved.toReminderConfigDTO()
    }

    @DeleteMapping("/config/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteReminder(@PathVariable id: UUID) {
        reminderConfigCrud.delete(id)
    }

    @ExceptionHandler(EntityNotFoundException::class) @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleENFE(e: EntityNotFoundException) = ErrorRes("Not found")

    @ExceptionHandler(IllegalArgumentException::class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIAE(e: IllegalArgumentException) = ErrorRes(e.message ?: "Une erreur est survenue")

}

fun ReminderConfigReq.toReminderConfig(id: UUID = UUID.randomUUID()) = ReminderConfig(
    id,
    type = this.type,
    hours = this.hours,
    templateMail = this.templateMail,
    templateSms = this.templateSms,
)

fun ReminderConfig.toReminderConfigDTO() = ReminderConfigDTO(
    id = this.id,
    type = this.type,
    hours = this.hours,
    templateMail = this.templateMail,
    templateSms = this.templateSms,
)