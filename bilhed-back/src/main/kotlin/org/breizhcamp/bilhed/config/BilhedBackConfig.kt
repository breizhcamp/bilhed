package org.breizhcamp.bilhed.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bilhed.back")
data class BilhedBackConfig(
    val breizhCampYear: Int
)
