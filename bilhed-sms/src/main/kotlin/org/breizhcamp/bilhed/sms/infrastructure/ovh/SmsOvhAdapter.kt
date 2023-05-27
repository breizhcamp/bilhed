package org.breizhcamp.bilhed.sms.infrastructure.ovh

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.breizhcamp.bilhed.sms.config.BilhedSmsConfig
import org.breizhcamp.bilhed.sms.config.OvhConfig
import org.breizhcamp.bilhed.sms.domain.use_cases.ports.SmsPort
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.Instant

private val logger = KotlinLogging.logger {}

private const val BASE_URL_SMS = "https://eu.api.ovh.com/1.0/sms"

@Service
@ConditionalOnProperty(prefix = "bilhed.sms", name = ["sendType"], havingValue = "OVH")
class SmsOvhAdapter(
    private val config: BilhedSmsConfig,
    private val objectMapper: ObjectMapper,
): SmsPort {

    @PostConstruct
    fun setup() {
        requireNotNull(config.ovh) { "OVH SMS provider enabled but OVH configuration is null" }
    }

    override fun send(phone: String, message: String, tag: String) {
        logger.info { "[OVH] Sending SMS to [${phone}] with message [${message}]" }

        val ovhConfig = config.ovh ?: throw IllegalStateException("OVH config is null")

        val ovhSms = SmsOvhDto(
            message,
            listOf(phone),
            ovhConfig.sender,
            true,
            "high",
            false,
            30,
            tag,
        ).let { objectMapper.writeValueAsString(it) }

        val res = createOvhRequest(ovhConfig, HttpMethod.POST, "/${ovhConfig.serviceName}/jobs", ovhSms)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        logger.info { "[OVH] SMS sent with response: $res" }
        //{"validReceivers":["+336123456789"],"ids":[123456789],"invalidReceivers":[],"totalCreditsRemoved":1}
    }

    private fun createOvhRequest(ovhConfig: OvhConfig, method: HttpMethod, uri: String, body: String): WebClient.RequestHeadersSpec<*> {
        val timestamp = Instant.now().epochSecond.toString()
        val toSign = "${ovhConfig.applicationSecret}+${ovhConfig.consumerKey}+${method.name()}+$BASE_URL_SMS$uri+$body+$timestamp"
        val signature = "\$1\$" + toSign.sha1()

        return createWebClient()
            .method(method)
            .uri(uri)
            .header("X-Ovh-Timestamp", timestamp)
            .header("X-Ovh-Signature", signature)
            .header("X-Ovh-Application", ovhConfig.applicationKey)
            .header("X-Ovh-Consumer", ovhConfig.consumerKey)
            .bodyValue(body)
    }

    private fun createWebClient(): WebClient {
//        val httpClient = HttpClient.create().wiretap("reactor.netty.http.client.HttpClient",
//            LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)

        return WebClient.builder()
//            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(BASE_URL_SMS)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    private fun String.sha1(): String {
        val md = java.security.MessageDigest.getInstance("SHA-1")
        return md.digest(this.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}