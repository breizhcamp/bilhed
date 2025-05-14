package org.breizhcamp.bilhed.infrastructure.sms

import org.breizhcamp.bilhed.domain.entities.Sms

fun Sms.toMQ() = SmsMQ(
    id = this.id,
    phone = this.phone,
    template = this.template,
    model = this.model,
)