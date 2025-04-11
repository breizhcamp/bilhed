package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Config
import org.breizhcamp.bilhed.infrastructure.db.model.ConfigDB

fun ConfigDB.toConfig() = Config(
    key = this.key,
    value = this.value,
)

fun Config.toDB() = ConfigDB(
    key = this.key,
    value = this.value,
)