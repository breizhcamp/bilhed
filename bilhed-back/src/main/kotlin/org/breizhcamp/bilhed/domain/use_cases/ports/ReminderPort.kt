package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Reminder

interface ReminderPort {

    fun save(reminder: Reminder)
}