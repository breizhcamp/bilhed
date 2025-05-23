package org.breizhcamp.bilhed.infrastructure.db

import org.breizhcamp.bilhed.domain.entities.Reminder
import org.breizhcamp.bilhed.domain.use_cases.ports.ReminderPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toReminder
import org.breizhcamp.bilhed.infrastructure.db.repos.ReminderRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReminderAdapter (
    val reminderRepo: ReminderRepo,
): ReminderPort {
    override fun save(reminder: Reminder) {
        reminderRepo.save(reminder.toDB())
    }

    override fun findLatestReminderPerPerson(personIds: List<UUID>): List<Reminder> {
        return reminderRepo.findLatestReminderPerPerson(personIds).map { it.toReminder() }
    }


}