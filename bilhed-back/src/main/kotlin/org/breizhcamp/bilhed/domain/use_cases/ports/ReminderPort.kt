package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Reminder
import java.util.UUID

interface ReminderPort {

    fun save(reminder: Reminder)

    fun findLatestReminderPerPerson(personIds: List<UUID>): List<Reminder>
}