package org.breizhcamp.bilhed.infrastructure.sms

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.use_cases.ports.SmsPort
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class SmsAdapter: SmsPort {
    override fun sendRegistered(registered: Registered) {
        logger.info { "Sending SMS to AMQP to [${registered.lastname} ${registered.firstname}] / " +
                "[${registered.telephone}] with token [${registered.token}]" }
    }
}