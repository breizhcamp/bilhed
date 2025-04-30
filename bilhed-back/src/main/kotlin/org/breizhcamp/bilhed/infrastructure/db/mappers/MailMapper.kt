package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.MailAddress
import org.breizhcamp.bilhed.infrastructure.db.model.MailAddressMQ
import org.breizhcamp.bilhed.infrastructure.db.model.MailMQ

fun Mail.toMQ() = MailMQ(
    to = this.to.map { it.toMQ() },
    template = this.template,
    model = this.model,
)

fun MailAddress.toMQ() = MailAddressMQ(
    email = this.email,
    name = this.name,
)