package org.breizhcamp.bilhed.sms.infrastructure.ovh

import mu.KotlinLogging
import org.breizhcamp.bilhed.sms.config.BilhedSmsConfig
import org.breizhcamp.bilhed.sms.domain.entities.Sms
import org.breizhcamp.bilhed.sms.domain.use_cases.ports.SmsPort
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Service
@ConditionalOnProperty(prefix = "bilhed.sms", name = ["sendType"], value = ["OVH"])
class SmsOvhAdapter(
    private val config: BilhedSmsConfig,
): SmsPort {
    override fun send(sms: Sms) {
        logger.info { "[OVH] Sending SMS to [${sms.phone}] with message [${sms.message}]" }

        val serviceName = config.ovh?.serviceName ?: throw IllegalStateException("OVH service name is not configured")

        val ovhSms = SmsOvhDto(
            serviceName,
            sms.message,
            listOf(sms.phone),
            "BreizhCamp",
            true,
            "high",
            false,
            30,
            "register",
        )

        createWebClient()
            .post()
            .uri("/serviceName/$serviceName/jobs")
            .bodyValue(ovhSms)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    private fun createWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://eu.api.ovh.com/1.0/sms")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}