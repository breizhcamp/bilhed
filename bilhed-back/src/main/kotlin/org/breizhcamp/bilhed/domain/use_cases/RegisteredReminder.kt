package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.use_cases.ports.MailPort
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RegisteredReminder(
    private val config: BilhedBackConfig,
    private val mailPort: MailPort,
    private val registeredPort: RegisteredPort,
) {

    @Transactional
    fun send(id: UUID, sms: Boolean, email: Boolean) {
        val registered = registeredPort.get(id)
        registeredPort.resetSmsCount(id)

        val link = "${config.participantFrontUrl}/#/${registered.id}"

        val model = mapOf("firstname" to registered.firstname, "lastname" to registered.lastname,
            "year" to config.breizhCampYear.toString(), "link" to link)

        mailPort.send(Mail(registered.getMailAddress(), "registration_reminder", model))
    }

}