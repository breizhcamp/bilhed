package org.breizhcamp.bilhed.infrastructure.db

import org.breizhcamp.bilhed.domain.entities.Reminder
import org.breizhcamp.bilhed.domain.use_cases.ports.ReminderPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.repos.ReminderRepo
import org.springframework.stereotype.Component

@Component
class ReminderAdapter (
    val reminderRepo: ReminderRepo,
): ReminderPort {
    override fun save(reminder: Reminder) {
        reminderRepo.save(reminder.toDB())
    }


}