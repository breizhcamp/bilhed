package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.Config
import org.breizhcamp.bilhed.domain.use_cases.ports.ConfigPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toConfig
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.repos.ConfigRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ConfigAdapter (
    private val configRepo: ConfigRepo
): ConfigPort {
    override fun list(): List<Config> = configRepo.findAll().map { it.toConfig() }

    override fun get(key: String): Config = configRepo.findByIdOrNull(key)?.toConfig() ?: throw EntityNotFoundException("Unable to find config [$key].")

    override fun save(config: Config) {
        configRepo.save(config.toDB())
    }

    override fun update(config: Config) {
        configRepo.save(config.toDB())
    }

    override fun delete(key: String) {
        configRepo.deleteById(key)
    }

}