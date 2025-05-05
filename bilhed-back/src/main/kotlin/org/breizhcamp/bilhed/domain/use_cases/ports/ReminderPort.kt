package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Reminder
import java.util.UUID

interface ReminderPort {

    fun save(reminder: Reminder)

    fun listByPersonId(personId: UUID): List<Reminder>
}