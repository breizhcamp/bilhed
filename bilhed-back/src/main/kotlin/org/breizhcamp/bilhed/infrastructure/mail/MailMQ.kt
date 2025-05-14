package org.breizhcamp.bilhed.infrastructure.mail

data class MailMQ(
    val to: List<MailAddressMQ>,
    val template: String,
    val model: Map<String, String>,
)

data class MailAddressMQ(
    val email: String,
    val name: String? = null,
)

