package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.Config
import org.breizhcamp.bilhed.domain.use_cases.ports.ConfigPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConfigCrud (
    val configPort: ConfigPort,
) {
    fun list(): List<Config> = configPort.list()

    fun add(config: Config): Config {
        configPort.save(config)
        return getByKey(config.key)
    }

    @Transactional
    fun update(config: Config) = configPort.update(config)

    @Transactional
    fun delete(key: String) = configPort.delete(key)

    fun getByKey(key: String): Config = configPort.get(key)

}