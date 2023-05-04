package org.breizhcamp.bilhed.mail.config

import org.breizhcamp.bilhed.mail.application.amqp.dto.MailAddressDTO
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bilhed.mail")
data class BilhedMailConfig(
    val from: MailAddressDTO,
)
