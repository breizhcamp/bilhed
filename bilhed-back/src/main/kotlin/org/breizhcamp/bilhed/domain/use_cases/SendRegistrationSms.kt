package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.entities.Sms
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}

@Service
class SendRegistrationSms(
    private val registeredPort: RegisteredPort,
    private val sendNotification: SendNotification
) {

    fun sendSms(registered: Registered): Registered {
        if (!registered.telephone.startsWith("+")) {
            logger.warn { "Trying to send sms to [${registered.telephone}] / [${registered.lastname} ${registered.firstname}] but phone number is not international" }
            throw IllegalArgumentException("Erreur interne, le téléphone n'est pas au format international")
        }

        if (registered.nbSmsSent >= 3) {
            logger.warn { "Trying to send sms to registered [${registered.lastname} ${registered.firstname}] but already sent 3 times" }
            throw IllegalArgumentException("Vous avez déjà demandé 3 fois un code par SMS. Veuillez contacter l'organisation.")
        }

        if (registered.smsStatus == SmsStatus.SENDING && registered.lastSmsSentDate?.plusSeconds(29)?.isAfter(ZonedDateTime.now()) == true) {
            logger.warn { "Trying to send sms to registered [${registered.lastname} ${registered.firstname}] but already sent less than 30 sec ago" }
            throw IllegalArgumentException("Un SMS a déjà été envoyé il y a moins de 30 secondes. Veuillez patienter.")
        }

        // add some delay to avoid sms flood
        Thread.sleep(1000)

        val res = registered.copy(
            smsStatus = SmsStatus.SENDING,
            nbSmsSent = registered.nbSmsSent + 1,
            lastSmsSentDate = ZonedDateTime.now(),
            token = registered.token
        )

        val sms = Sms(
            id = registered.id,
            phone = registered.telephone,
            template = "registered_token",
            model = mapOf("token" to registered.token),
        )

        sendNotification.sendSms(sms, ReminderOrigin.MANUAL)
        registeredPort.save(res)
        return res
    }
}