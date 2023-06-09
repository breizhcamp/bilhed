package org.breizhcamp.bilhed.sms.infrastructure.console

import mu.KotlinLogging
import org.breizhcamp.bilhed.sms.domain.use_cases.ports.SmsPort
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
@ConditionalOnProperty(prefix = "bilhed.sms", name = ["sendType"], havingValue = "CONSOLE")
class SmsConsoleAdapter: SmsPort {
    override fun send(phone: String, message: String, tag: String) {
        logger.info { "[CONSOLE] Sending SMS to [${phone}] with message [${message}] and tag [${tag}]" }
    }
}