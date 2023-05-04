package org.breizhcamp.bilhed.mail.application.amqp.dto

data class MailFromBack(
    val to: List<MailAddressDTO>,
    val template: String,
    val model: Map<String, String>,
)

data class MailAddressDTO(
    val email: String,
    val name: String? = null,
)
