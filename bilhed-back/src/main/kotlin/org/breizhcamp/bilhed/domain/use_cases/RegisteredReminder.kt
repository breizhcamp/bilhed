package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.entities.Sms
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.breizhcamp.bilhed.domain.use_cases.ports.UrlShortenerPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class RegisteredReminder(
    private val config: BilhedBackConfig,
    private val registeredPort: RegisteredPort,
    private val urlShortenerPort: UrlShortenerPort,
    private val sendNotification: SendNotification
) {

    @Transactional
    fun send(id: UUID, smsTemplate: String, emailTemplate: String, origin: ReminderOrigin) {
        if (smsTemplate.isBlank() && emailTemplate.isBlank()) return

        val registered = registeredPort.get(id)
        registeredPort.resetSmsCount(id)
        val link = "${config.participantFrontUrl}/#/${registered.id}"

        if (!emailTemplate.isBlank()) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")
            val closeDate = dateFormatter.format(config.registerCloseDate.withZoneSameInstant(ZoneId.of("Europe/Paris")))

            val model = mapOf("firstname" to registered.firstname, "lastname" to registered.lastname,
                "year" to config.breizhCampYear.toString(), "link" to link, "closeDate" to closeDate)

            sendNotification.sendEmail(Mail(registered.getMailAddress(), emailTemplate, model, id), origin)
        }

        if (!smsTemplate.isBlank()) {
            val shortLink = urlShortenerPort.shorten(link, config.registerCloseDate)
            val smsModel = mapOf("link" to shortLink, "token" to registered.token)
            sendNotification.sendSms(Sms(registered.id, registered.telephone, smsTemplate, smsModel), origin)
        }
    }

}