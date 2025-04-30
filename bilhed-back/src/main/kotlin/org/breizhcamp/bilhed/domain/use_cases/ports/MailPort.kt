package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.infrastructure.db.model.MailMQ

interface MailPort {

    fun send(mail: MailMQ)

}