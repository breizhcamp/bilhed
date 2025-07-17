package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.entities.Sms
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ReferentInfosPort
import org.breizhcamp.bilhed.domain.use_cases.ports.UrlShortenerPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class RegisteredReminder(
    private val config: BilhedBackConfig,
    private val personPort: PersonPort,
    private val urlShortenerPort: UrlShortenerPort,
    private val sendNotification: SendNotification,
    private val referentInfosPort: ReferentInfosPort
) {

    @Transactional
    fun send(id: UUID, smsTemplate: String, emailTemplate: String, origin: ReminderOrigin) {
        if (smsTemplate.isBlank() && emailTemplate.isBlank()) return

        val ref = personPort.get(id)
        val refInfos = referentInfosPort.get(id)

        referentInfosPort.resetSmsCount(id)
        val link = "${config.participantFrontUrl}/#/${ref.id}"

        if (!emailTemplate.isBlank()) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")
            val closeDate = dateFormatter.format(config.registerCloseDate.withZoneSameInstant(ZoneId.of("Europe/Paris")))

            val model = mapOf("firstname" to ref.firstname, "lastname" to ref.lastname,
                "year" to config.breizhCampYear.toString(), "link" to link, "closeDate" to closeDate)

            sendNotification.sendEmail(Mail(ref.getMailAddress(), emailTemplate, model, id), origin)
        }

        if (!smsTemplate.isBlank()) {
            val shortLink = urlShortenerPort.shorten(link, config.registerCloseDate)
            val smsModel = mapOf("link" to shortLink, "token" to refInfos.token)
            if (ref.telephone != null)
                sendNotification.sendSms(Sms(ref.id, ref.telephone, smsTemplate, smsModel), origin)
        }
    }

}