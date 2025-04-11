package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity @Table(name = "bilhed_config")
data class ConfigDB (

    @Id
    val key: String,
    val value: String
)