package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Mail

interface MailPort {

    fun send(mail: Mail)

}