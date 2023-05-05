package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.MailAddress
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.MailPort
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Register a new participant to the lottery
 */
@Service
class Registration(
    private val config: BilhedBackConfig,
    private val registeredPort: RegisteredPort,
    private val mailPort: MailPort,
    private val smsSend: SendSms,
) {

    @Transactional
    fun register(registered: Registered): Registered {
        if (registeredPort.existsEmailOrPhone(registered.email, registered.telephone)) {
            throw IllegalArgumentException("Une inscription avec cet email ou ce téléphone existe déjà.")
        }

        logger.info { "Creating new registered [${registered.lastname} ${registered.firstname}] with email [${registered.email}]" }

        val regSms = smsSend.sendSms(registered)
        registeredPort.save(regSms)

        logger.info { "New registered [${registered.lastname} ${registered.firstname}] created" }

        return registered
    }

    fun get(id: UUID): Registered = registeredPort.get(id)

    @Transactional
    fun changePhoneNumber(id: UUID, phone: String) {
        val registered = get(id)

        if (registeredPort.existPhone(phone)) {
            logger.warn { "Trying to change phone number of registered [$id][${registered.lastname} ${registered.firstname}] to [$phone] but this phone number is already used" }
            throw IllegalArgumentException("Une inscription avec cet email ou ce téléphone existe déjà.")
        }

        logger.info { "Changing phone number of registered [${registered.lastname} ${registered.firstname}] to [$phone]" }

        val regSms = smsSend.sendSms(registered.copy(telephone = phone))
        registeredPort.save(regSms)
    }

    @Transactional
    fun resendSms(id: UUID) {
        registeredPort.save(smsSend.sendSms(get(id)))
    }

    @Transactional
    fun saveSmsStatus(id: UUID, error: String?) {
        val registered = get(id)
        val smsStatus = if (error == null) SmsStatus.SENT else SmsStatus.ERROR
        registeredPort.save(registered.copy(smsStatus = smsStatus, smsError = error))
    }

    @Transactional
    fun validateToken(id: UUID, code: String) {
        if (!code.matches("^[0-9]{6}\$".toRegex())) throw IllegalArgumentException("Le code saisi est invalide")
        val registered = get(id)
        if (code != registered.token) throw IllegalArgumentException("Le code saisi est invalide")

        registeredPort.levelUpToParticipant(id)

        val model = mapOf("firstname" to registered.firstname, "lastname" to registered.lastname, "year" to config.breizhCampYear.toString())
        mailPort.send(Mail(registered.getMailAddress(), "register", model))

        logger.info { "Validated [${registered.lastname} ${registered.firstname}] as a participant" }
    }

    fun Registered.getMailAddress() = listOf(MailAddress(email, "$firstname $lastname"))
}