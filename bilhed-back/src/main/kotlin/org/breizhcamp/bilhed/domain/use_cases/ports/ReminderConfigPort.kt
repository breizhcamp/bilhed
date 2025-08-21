package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.ReminderConfig
import java.util.*

interface ReminderConfigPort {

    fun list(): List<ReminderConfig>

    fun get(id: UUID): ReminderConfig

    fun save(reminderConfig: ReminderConfig)

    fun update(reminderConfig: ReminderConfig)

    fun delete(id: UUID)

    fun countBy(type: String): Int

    fun listBy(type: String): List<ReminderConfig>
}