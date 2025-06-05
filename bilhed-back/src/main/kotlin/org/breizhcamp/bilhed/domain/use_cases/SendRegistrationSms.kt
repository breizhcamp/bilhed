package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.Referent
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

    fun sendSms(referent: Referent): Person {
        if (referent.person.telephone == null || !referent.person.telephone.startsWith("+")) {
            logger.warn { "Trying to send sms to [${referent.person.telephone}] / [${referent.person.lastname} ${referent.person.firstname}] but phone number is not international" }
            throw IllegalArgumentException("Erreur interne, le téléphone n'est pas au format international")
        }

        if (referent.referentInfos.nbSmsSent >= 3) {
            logger.warn { "Trying to send sms to registered [${referent.person.lastname} ${referent.person.firstname}] but already sent 3 times" }
            throw IllegalArgumentException("Vous avez déjà demandé 3 fois un code par SMS. Veuillez contacter l'organisation.")
        }

        if (referent.referentInfos.smsStatus == SmsStatus.SENDING && referent.referentInfos.lastSmsSentDate?.plusSeconds(29)?.isAfter(ZonedDateTime.now()) == true) {
            logger.warn { "Trying to send sms to registered [${referent.person.lastname} ${referent.person.firstname}] but already sent less than 30 sec ago" }
            throw IllegalArgumentException("Un SMS a déjà été envoyé il y a moins de 30 secondes. Veuillez patienter.")
        }

        Thread.sleep(1000)

        val refInfos = referent.referentInfos.copy(
            smsStatus = SmsStatus.SENDING,
            nbSmsSent = referent.referentInfos.nbSmsSent + 1,
            lastSmsSentDate = ZonedDateTime.now(),
            token = referent.referentInfos.token
        )

        val sms = Sms(
            id = referent.referentInfos.personId,
            phone = referent.person.telephone,
            template = "registered_token",
            model = mapOf("token" to referent.referentInfos.token),
        )

        val ref = referent.copy(referentInfos = refInfos)

        sendNotification.sendSms(sms, ReminderOrigin.MANUAL)
        referentInfosPort.save(refInfos)
        return ref.person
    }
}