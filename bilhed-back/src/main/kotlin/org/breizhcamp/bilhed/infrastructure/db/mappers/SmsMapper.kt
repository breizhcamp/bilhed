package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Sms
import org.breizhcamp.bilhed.infrastructure.db.model.SmsMQ

fun Sms.toMQ() = SmsMQ(
    id = this.id,
    phone = this.phone,
    template = this.template,
    model = this.model,
)