package org.breizhcamp.bilhed.sms.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bilhed.sms")
data class BilhedSmsConfig(
    val sendType: String = "CONSOLE",
    val ovh: OvhConfig? = null,
)

data class OvhConfig(
    val serviceName: String,
    val sender: String,

    val applicationKey: String,
    val applicationSecret: String,
    val consumerKey: String,
)
