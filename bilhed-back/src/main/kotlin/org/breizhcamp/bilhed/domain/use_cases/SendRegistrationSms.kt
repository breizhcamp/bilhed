package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.ReferentInfos
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.entities.Sms
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.ReferentInfosPort
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}

@Service
class SendRegistrationSms(
    private val sendNotification: SendNotification,
    private val referentInfosPort: ReferentInfosPort
) {

    fun sendSms(ref: Person, refInfos: ReferentInfos): Person {
        if (ref.telephone == null || !ref.telephone.startsWith("+")) {
            logger.warn { "Trying to send sms to [${ref.telephone}] / [${ref.lastname} $ref.firstname}] but phone number is not international" }
            throw IllegalArgumentException("Erreur interne, le téléphone n'est pas au format international")
        }

        if (refInfos.nbSmsSent >= 3) {
            logger.warn { "Trying to send sms to registered [${ref.lastname} ${ref.firstname}] but already sent 3 times" }
            throw IllegalArgumentException("Vous avez déjà demandé 3 fois un code par SMS. Veuillez contacter l'organisation.")
        }

        if (refInfos.smsStatus == SmsStatus.SENDING && refInfos.lastSmsSentDate?.plusSeconds(29)?.isAfter(ZonedDateTime.now()) == true) {
            logger.warn { "Trying to send sms to registered [${ref.lastname} ${ref.firstname}] but already sent less than 30 sec ago" }
            throw IllegalArgumentException("Un SMS a déjà été envoyé il y a moins de 30 secondes. Veuillez patienter.")
        }

        Thread.sleep(1000)

        val newRefInfos = refInfos.copy(
            smsStatus = SmsStatus.SENDING,
            nbSmsSent = refInfos.nbSmsSent + 1,
            lastSmsSentDate = ZonedDateTime.now(),
            token = refInfos.token
        )

        val sms = Sms(
            id = refInfos.personId,
            phone = ref.telephone,
            template = "registered_token",
            model = mapOf("token" to refInfos.token),
        )

        sendNotification.sendSms(sms, ReminderOrigin.MANUAL)
        referentInfosPort.save(newRefInfos)
        return ref
    }
}