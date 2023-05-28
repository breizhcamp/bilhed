package org.breizhcamp.bilhed.config

import org.breizhcamp.bilhed.domain.entities.PassType
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.ZonedDateTime

@ConfigurationProperties(prefix = "bilhed.back")
data class BilhedBackConfig(
    val breizhCampYear: Int,
    val participantFrontUrl: String,
    val registerCloseDate: ZonedDateTime,

    val passNumber: Map<PassType, Int>,

    val bihan: BihanConfig,
)

data class BihanConfig(
    val url: String,
    val apiKey: String,
)
