package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.ReminderConfig
import org.breizhcamp.bilhed.domain.use_cases.ports.ReminderConfigPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toReminderConfig
import org.breizhcamp.bilhed.infrastructure.db.repos.ReminderConfigRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReminderConfigAdapter (
    private val reminderConfigRepo: ReminderConfigRepo,
): ReminderConfigPort {
    override fun list(): List<ReminderConfig> = reminderConfigRepo.findAll().map { it.toReminderConfig() }

    override fun get(id: UUID): ReminderConfig {
        return reminderConfigRepo.findByIdOrNull(id)?.toReminderConfig() ?: throw EntityNotFoundException()
    }

    override fun save(reminderConfig: ReminderConfig) {
        reminderConfigRepo.save(reminderConfig.toDB())
    }

    override fun update(reminderConfig: ReminderConfig) {
        reminderConfigRepo.save(reminderConfig.toDB())
    }

    override fun delete(id: UUID) {
        reminderConfigRepo.deleteById(id)
    }

    override fun countByType(type: String): Int {
        return reminderConfigRepo.countByType(type)
    }
}