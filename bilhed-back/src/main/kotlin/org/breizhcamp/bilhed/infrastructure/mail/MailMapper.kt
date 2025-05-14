package org.breizhcamp.bilhed.infrastructure.mail

import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.MailAddress

fun Mail.toMQ() = MailMQ(
    to = this.to.map { it.toMQ() },
    template = this.template,
    model = this.model,
)

fun MailAddress.toMQ() = MailAddressMQ(
    email = this.email,
    name = this.name,
)