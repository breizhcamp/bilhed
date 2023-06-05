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
    val breizhCampCloseDate: ZonedDateTime,

    val passNumber: Map<PassType, Int>,

    val bihan: BihanConfig,
    val billetWeb: BilletWeb,
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
    val passPrices: Map<PassType, BigDecimal>,
)