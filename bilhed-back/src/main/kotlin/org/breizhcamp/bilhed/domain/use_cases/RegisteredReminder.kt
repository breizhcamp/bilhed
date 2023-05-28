package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.Sms
import org.breizhcamp.bilhed.domain.use_cases.ports.MailPort
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.breizhcamp.bilhed.domain.use_cases.ports.SmsPort
import org.breizhcamp.bilhed.domain.use_cases.ports.UrlShortenerPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class RegisteredReminder(
    private val config: BilhedBackConfig,
    private val mailPort: MailPort,
    private val smsPort: SmsPort,
    private val registeredPort: RegisteredPort,
    private val urlShortenerPort: UrlShortenerPort,
) {

    @Transactional
    fun send(id: UUID, sms: Boolean, email: Boolean) {
        if (!sms && !email) return

        val registered = registeredPort.get(id)
        registeredPort.resetSmsCount(id)
        val link = "${config.participantFrontUrl}/#/${registered.id}"

        if (email) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")
            val closeDate = dateFormatter.format(config.registerCloseDate.withZoneSameInstant(ZoneId.of("Europe/Paris")))

            val model = mapOf("firstname" to registered.firstname, "lastname" to registered.lastname,
                "year" to config.breizhCampYear.toString(), "link" to link, "closeDate" to closeDate)

            mailPort.send(Mail(registered.getMailAddress(), "registration_reminder", model))
        }

        if (sms) {
            val shortLink = urlShortenerPort.shorten(link, config.registerCloseDate)
            val smsModel = mapOf("link" to shortLink, "token" to registered.token)
            smsPort.send(Sms(registered.id, registered.telephone, "registration_reminder", smsModel))
        }
    }

}