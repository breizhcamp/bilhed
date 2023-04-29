package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.breizhcamp.bilhed.domain.use_cases.ports.SmsPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Register a new participant to the lottery
 */
@Service
class Registration(
    private val registeredPort: RegisteredPort,
    private val smsPort: SmsPort,
) {

    @Transactional
    fun register(registered: Registered): Registered {
        if (registeredPort.existsEmailOrPhone(registered.email, registered.telephone)) {
            throw IllegalArgumentException("Une inscription avec cet email ou ce téléphone existe déjà.")
        }

        logger.info { "Creating new registered [${registered.lastname} ${registered.firstname}] with email [${registered.email}]" }

        val regSms = sendSms(registered)
        registeredPort.save(regSms)

        logger.info { "New registered [${registered.lastname} ${registered.firstname}] created" }

        return registered
    }

    fun get(id: UUID): Registered = registeredPort.get(id)

    fun changePhoneNumber(id: UUID, phone: String) {
        val registered = get(id)

        if (registeredPort.existPhone(phone)) {
            logger.warn { "Trying to change phone number of registered [$id][${registered.lastname} ${registered.firstname}] to [$phone] but this phone number is already used" }
            throw IllegalArgumentException("Une inscription avec cet email ou ce téléphone existe déjà.")
        }

        logger.info { "Changing phone number of registered [${registered.lastname} ${registered.firstname}] to [$phone]" }

        val regSms = sendSms(registered.copy(telephone = phone))
        registeredPort.save(regSms)
    }

    fun resendSms(id: UUID) {
        registeredPort.save(sendSms(get(id)))
    }

    @Transactional
    fun validateToken(id: UUID, code: String) {
        if (!code.matches("^[0-9]{6}\$".toRegex())) throw IllegalArgumentException("Le code saisi est invalide")
        val registered = get(id)
        if (code != registered.token) throw IllegalArgumentException("Le code saisi est invalide")

        registeredPort.levelUpToParticipant(id)

        logger.info { "Validated [${registered.lastname} ${registered.firstname}] as a participant" }
    }
    private fun sendSms(registered: Registered): Registered {
        if (registered.nbSmsSent >= 3) {
            logger.warn { "Trying to send sms to registered [${registered.lastname} ${registered.firstname}] but already sent 3 times" }
            throw IllegalArgumentException("Vous avez déjà demandé 3 fois un code par SMS. Veuillez contacter l'organisation.")
        }

        val res = registered.copy(
            smsStatus = SmsStatus.SENDING,
            nbSmsSent = registered.nbSmsSent + 1,
            lastSmsSentDate = ZonedDateTime.now(),
            token = registered.token ?: genSmsToken()
        )

        smsPort.sendRegistered(res)
        return res
    }
    private fun genSmsToken(): String = RandomStringUtils.randomNumeric(6)
}