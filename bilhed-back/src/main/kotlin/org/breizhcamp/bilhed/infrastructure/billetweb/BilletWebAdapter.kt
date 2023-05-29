package org.breizhcamp.bilhed.infrastructure.billetweb

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.handler.logging.LogLevel
import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.PayStatus
import org.breizhcamp.bilhed.domain.entities.Ticket
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.breizhcamp.bilhed.infrastructure.billetweb.dto.CreateCmd
import org.breizhcamp.bilhed.infrastructure.billetweb.dto.CreateProduct
import org.breizhcamp.bilhed.infrastructure.billetweb.dto.CreateReq
import org.breizhcamp.bilhed.infrastructure.db.model.BilletWebDB
import org.breizhcamp.bilhed.infrastructure.db.repos.BilletWebRepo
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.stereotype.Component
import org.springframework.util.MimeType
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId

private val logger = KotlinLogging.logger {}

@Component
class BilletWebAdapter(
    private val config: BilhedBackConfig,
    private val objectMapper: ObjectMapper,
    private val billetWebRepo: BilletWebRepo,
): TicketPort {

    private val billetWebClient = createClient()

    override fun create(participant: Participant): Ticket {
        logger.info { "[BilletWeb] Create ticket for participant [${participant.id}] / [${participant.lastname}] [${participant.firstname}]" }
        val createRes = billetWebClient.create(config.billetWeb.eventId, CreateReq(participant.toCreateReq())).firstOrNull()
            ?: throw IllegalStateException("Impossible de créer la commande BilletWeb, merci de contacter l'équipe")
        logger.info { "[BilletWeb] Ticket created for participant [${participant.id}] / [${participant.lastname}] [${participant.firstname}] with res: $createRes" }

        val productId = createRes.products.firstOrNull()?.toString()
            ?: throw IllegalStateException("Impossible de créer la commande BilletWeb (pas de product), merci de contacter l'équipe")

        val lastUpdate = Instant.now().minusSeconds(10)
        logger.info { "[BilletWeb] Retrieving attendees updated after " + lastUpdate.atZone(ZoneId.of("Europe/Paris")) }
        val attendees = billetWebClient.listAttendees(config.billetWeb.eventId, lastUpdate.epochSecond)
        logger.info { "[BilletWeb] [${attendees.size}] Attendees retrieved" }

        val attendee = attendees.find { it.id == productId } ?: throw IllegalStateException("Impossible de retrouver la commande BilletWeb, merci de contacter l'équipe")
        logger.info { "[BilletWeb] Attendee found, id commande [${attendee.orderExtId}]" }

        val billetWeb = BilletWebDB(participant.id, createRes.id, attendee.orderManagement)
        billetWebRepo.save(billetWeb)

        return Ticket("${attendee.orderManagement}&action=pay", PayStatus.TO_PAY)
    }

    private fun Participant.toCreateReq() = CreateCmd(
        name = lastname,
        firstname = firstname,
        email = email,
        paymentType = "reservation",
        products = listOf(CreateProduct(
            ticket = requireNotNull(config.billetWeb.passNames[pass]) { "No BilletWeb pass name found for pass type [$pass]" },
            name = lastname,
            firstname = firstname,
            email = email,
        ))
    )

    private fun createClient(): BilletWebClient {
        /** In order to override incorrect text/html returned by BilletWeb... */
        val strategies = ExchangeStrategies.builder().codecs { clientCodecConfigurer ->
            clientCodecConfigurer.customCodecs().register(
                Jackson2JsonDecoder(objectMapper, MimeType("text", "html", StandardCharsets.UTF_8))
            )
        }.build()

        val httpClient = HttpClient.create().wiretap("reactor.netty.http.client.HttpClient",
            LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)

        val webClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies)
            .defaultHeader("Authorization", "Basic ${config.billetWeb.apiKey}")
            .baseUrl(config.billetWeb.url).build()

        val factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient)).build()

        return factory.createClient(BilletWebClient::class.java)
    }
}