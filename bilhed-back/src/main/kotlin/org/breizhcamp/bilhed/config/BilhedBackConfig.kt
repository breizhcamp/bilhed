package org.breizhcamp.bilhed.config

import org.breizhcamp.bilhed.domain.entities.PassType
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bilhed.back")
data class BilhedBackConfig(
    val breizhCampYear: Int,

    val passNumber: Map<PassType, Int>,
)
