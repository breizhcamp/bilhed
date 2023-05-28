package org.breizhcamp.bilhed.infrastructure.bihan

import mu.KotlinLogging
import org.breizhcamp.bihan.application.dto.AddLinkReq
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.use_cases.ports.UrlShortenerPort
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}

@Component
class BihanShortenerAdapter(
    private val config: BilhedBackConfig,
): UrlShortenerPort {

    private val bihanClient = createClient()

    override fun shorten(url: String, expirationDate: ZonedDateTime?): String {
        logger.info { "Create short url [${url}] with expirationDate [${expirationDate}]" }
        val req = AddLinkReq(url, expirationDate)
        val linkId = bihanClient.createLink(req).id
        val link = "${config.bihan.url}/${linkId}"
        logger.info { "Short url created with [${link}] for link [$url]" }
        return link
    }

    private fun createClient(): BihanClient {
        val webClient = WebClient.builder().baseUrl(config.bihan.url)
            .defaultHeader("Authorization", "ApiKey ${config.bihan.apiKey}").build()

        val factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient)).build()
        return factory.createClient(BihanClient::class.java)
    }
}