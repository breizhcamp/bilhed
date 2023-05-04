package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.SmsPort
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}

@Service
class SendSms(
    private val smsPort: SmsPort,
) {

    fun sendSms(registered: Registered): Registered {
        if (registered.nbSmsSent >= 3) {
            logger.warn { "Trying to send sms to registered [${registered.lastname} ${registered.firstname}] but already sent 3 times" }
            throw IllegalArgumentException("Vous avez déjà demandé 3 fois un code par SMS. Veuillez contacter l'organisation.")
        }

        if (registered.smsStatus == SmsStatus.SENDING && registered.lastSmsSentDate?.plusSeconds(29)?.isAfter(ZonedDateTime.now()) == true) {
            logger.warn { "Trying to send sms to registered [${registered.lastname} ${registered.firstname}] but already sent less than 30 sec ago" }
            throw IllegalArgumentException("Un SMS a déjà été envoyé il y a moins de 30 secondes. Veuillez patienter.")
        }

        val res = registered.copy(
            smsStatus = SmsStatus.SENDING,
            nbSmsSent = registered.nbSmsSent + 1,
            lastSmsSentDate = ZonedDateTime.now(),
            token = registered.token ?: genSmsToken()
        )

        // add some delay to avoid sms flood
        Thread.sleep(1000)

        smsPort.sendRegistered(res)
        return res
    }
    private fun genSmsToken(): String = RandomStringUtils.randomNumeric(6)

}