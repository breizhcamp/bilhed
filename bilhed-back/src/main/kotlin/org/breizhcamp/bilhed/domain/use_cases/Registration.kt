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

    private fun sendSms(registered: Registered): Registered {
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