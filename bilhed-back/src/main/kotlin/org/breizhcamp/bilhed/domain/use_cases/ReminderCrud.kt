package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.Reminder
import org.breizhcamp.bilhed.domain.use_cases.ports.ReminderPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReminderCrud (
    private val reminderPort : ReminderPort
) {
    fun listByPersonId(id: UUID): List<Reminder> {
        return reminderPort.listByPersonId(id)
    }
}