package org.breizhcamp.bilhed.config

import org.breizhcamp.bilhed.domain.entities.PassType
import org.springframework.boot.context.properties.ConfigurationProperties
import java.math.BigDecimal
import java.time.ZonedDateTime

@ConfigurationProperties(prefix = "bilhed.back")
data class BilhedBackConfig(
    val breizhCampYear: Int,
    val participantFrontUrl: String,
    val registerCloseDate: ZonedDateTime,
    val breizhCampOpenDate: ZonedDateTime,
    val breizhCampCloseDate: ZonedDateTime,

    val bihan: BihanConfig,
    val billetWeb: BilletWeb,

    val templates: Templates
)

data class BihanConfig(
    val url: String,
    val apiKey: String,
)

data class BilletWeb(
    val enabled: Boolean,
    val url: String,

    val eventId: String?,
    val apiKey: String?,

    val passNames: Map<PassType, String>,
)

data class Templates(
    val mail: Map<String, Boolean>,
    val sms: Map<String, Boolean>,
)