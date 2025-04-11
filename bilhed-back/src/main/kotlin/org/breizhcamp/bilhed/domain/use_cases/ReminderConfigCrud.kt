package org.breizhcamp.bilhed.domain.use_cases

import org.apache.commons.lang3.StringUtils.lowerCase
import org.breizhcamp.bilhed.domain.entities.ReminderConfig
import org.breizhcamp.bilhed.domain.use_cases.ports.ConfigPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ReminderConfigPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ReminderConfigCrud (
    val reminderConfigPort : ReminderConfigPort,
    val configPort : ConfigPort
) {
    fun list(): List<ReminderConfig> = reminderConfigPort.list()

    fun add(reminderConfig: ReminderConfig): ReminderConfig? {
        val nbReminders = reminderConfigPort.countByType(reminderConfig.type)
        val shortType = reminderConfig.type[0].plus(lowerCase(reminderConfig.type.substring(1, 3))) // REGISTERED -> Reg
        val type = configPort.get("reminderTime$shortType")

        if (nbReminders < 10 && reminderConfig.hours >= 0 && reminderConfig.hours <= type.value.toInt()) {
            reminderConfigPort.save(reminderConfig)
            return getByID(reminderConfig.id)
        }
        return null
    }

    @Transactional
    fun update(reminderConfig: ReminderConfig): ReminderConfig {
        val shortType = reminderConfig.type[0].plus(lowerCase(reminderConfig.type.substring(1, 3))) // REGISTERED -> Reg
        val type = configPort.get("reminderTime$shortType")
        if (reminderConfig.hours >= 0 && reminderConfig.hours <= type.value.toInt()) {
            reminderConfigPort.update(reminderConfig)
        }
        return getByID(reminderConfig.id)
    }

    @Transactional
    fun delete(id: UUID) = reminderConfigPort.delete(id)

    fun getByID(id: UUID): ReminderConfig = reminderConfigPort.get(id)

}