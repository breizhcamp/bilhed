package org.breizhcamp.bilhed.infrastructure.db.model

data class MailMQ(
    val to: List<MailAddressMQ>,
    val template: String,
    val model: Map<String, String>,
)

data class MailAddressMQ(
    val email: String,
    val name: String? = null,
)

