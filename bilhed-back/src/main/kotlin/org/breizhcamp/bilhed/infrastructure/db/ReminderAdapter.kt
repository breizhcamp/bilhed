package org.breizhcamp.bilhed.infrastructure.db

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.Reminder
import org.breizhcamp.bilhed.domain.use_cases.ports.ReminderPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toReminder
import org.breizhcamp.bilhed.infrastructure.db.repos.ReminderRepo
import org.springframework.stereotype.Component
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class ReminderAdapter (
    val reminderRepo: ReminderRepo,
): ReminderPort {
    override fun save(reminder: Reminder) {
        reminderRepo.save(reminder.toDB())
    }

    override fun listByPersonId(personId: UUID): List<Reminder> {
        val reminders = reminderRepo.findByPersonId(personId).map { it.toReminder() }
        logger.info { reminders.toString() }
        logger.info { reminders.size.toString() }
        return reminders
    }


}