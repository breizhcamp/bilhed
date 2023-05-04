package org.breizhcamp.bilhed.mail.domain.use_cases.port

import org.breizhcamp.bilhed.mail.domain.entities.Mail

interface EmailSenderPort {

    fun send(mail: Mail)

}