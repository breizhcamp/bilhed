package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Config

interface ConfigPort {

    fun list(): List<Config>

    fun get(key: String): Config

    fun save(config: Config)

    fun update(config: Config)

    fun delete(key: String)
}