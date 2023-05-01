package org.breizhcamp.bilhed.sms.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bilhed.sms")
data class BilhedSmsConfig(
    val sendType: SendType = SendType.CONSOLE,
    val ovh: OvhConfig? = null,
)

data class OvhConfig(
    val serviceName: String,
)

enum class SendType {
    CONSOLE,
    OVH,
}
