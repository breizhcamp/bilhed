package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Config
import org.breizhcamp.bilhed.domain.entities.PassType
import java.math.BigDecimal

interface ConfigPort {

    fun list(): List<Config>

    fun get(key: String): Config

    fun save(config: Config)

    fun update(config: Config)

    fun delete(key: String)

    fun getPassPrices(): Map<PassType, BigDecimal>
}