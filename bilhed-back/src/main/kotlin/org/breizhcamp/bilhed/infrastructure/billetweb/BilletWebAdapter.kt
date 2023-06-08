package org.breizhcamp.bilhed.infrastructure.billetweb

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityNotFoundException
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.stereotype.Component
import org.springframework.util.MimeType
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty(prefix = "bilhed.back", name = ["billetWeb.enabled"], havingValue = "true")
class BilletWebAdapter(
    private val config: BilhedBackConfig,
    private val objectMapper: ObjectMapper,
    private val billetWebRepo: BilletWebRepo,
): TicketPort {

    private val billetWebClient = createClient()

    @PostConstruct
    fun setup() {
        logger.info { "[BilletWeb] Using BilletWeb for ticket creation" }
    }

    override fun create(participant: Participant): Ticket {
        val eventId = requireNotNull(config.billetWeb.eventId) { "Erreur config, impossible de créer de Billet sans eventId" }

        logger.info { "[BilletWeb] Create ticket for participant [${participant.id}] / [${participant.lastname}] [${participant.firstname}]" }
        val createRes = billetWebClient.create(eventId, CreateReq(participant.toCreateReq())).firstOrNull()
            ?: throw IllegalStateException("Impossible de créer la commande BilletWeb, merci de contacter l'équipe")
        logger.info { "[BilletWeb] Ticket created for participant [${participant.id}] / [${participant.lastname}] [${participant.firstname}] with res: $createRes" }

        val productId = createRes.products.firstOrNull()?.toString()
            ?: throw IllegalStateException("Impossible de créer la commande BilletWeb (pas de product), merci de contacter l'équipe")

        val lastUpdate = Instant.now().minusSeconds(10)
        logger.info { "[BilletWeb] Retrieving attendees updated after " + lastUpdate.atZone(ZoneId.of("Europe/Paris")) }
        val attendees = billetWebClient.listAttendees(eventId, lastUpdate.epochSecond)
        logger.info { "[BilletWeb] [${attendees.size}] Attendees retrieved" }

        val attendee = attendees.find { it.id == productId } ?: throw IllegalStateException("Impossible de retrouver la commande BilletWeb, merci de contacter l'équipe")
        logger.info { "[BilletWeb] Attendee found, id commande [${attendee.orderExtId}]" }

        val billetWeb = BilletWebDB(participant.id, createRes.id, attendee.orderManagement)
        billetWebRepo.save(billetWeb)

        return Ticket(buildPayUrl(attendee.orderManagement), PayStatus.TO_PAY)
    }

    override fun getPayUrl(id: UUID): String {
        val billetWebInfo = billetWebRepo.findByIdOrNull(id)
            ?: throw EntityNotFoundException("Impossible de retrouver la commande BilletWeb, merci de contacter l'équipe")

        return buildPayUrl(billetWebInfo.orderManagerUrl)
    }

    override fun getPayed(): List<UUID> {
        val eventId = requireNotNull(config.billetWeb.eventId) { "Erreur config, impossible de sync sans eventId" }
        val attendees = billetWebClient.listAttendees(eventId).filter { it.orderPaid == "1" }
        val tickets = billetWebRepo.findAll().groupBy { it.attendeeId }

        return attendees.mapNotNull { tickets[it.orderId] }.flatten().map { it.participantId }
    }

    private fun buildPayUrl(orderManagement: String) = "$orderManagement&action=pay"



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
            price = requireNotNull(config.billetWeb.passPrices[pass]) { "No BilletWeb pass price found for pass type [$pass]" },
        ))
    )

    private fun createClient(): BilletWebClient {
        val apiKey = requireNotNull(config.billetWeb.apiKey) { "Config error, BilletWeb apiKey is missing" }

        /** In order to override incorrect text/html returned by BilletWeb... */
        val strategies = ExchangeStrategies.builder().codecs { clientCodecConfigurer ->
            clientCodecConfigurer.customCodecs().register(
                Jackson2JsonDecoder(objectMapper, MimeType("text", "html", StandardCharsets.UTF_8))
            )
        }.build()

//        val httpClient = HttpClient.create().wiretap("reactor.netty.http.client.HttpClient",
//            LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)

        val webClient = WebClient.builder()
//            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies)
            .defaultHeader("Authorization", "Basic ${apiKey}")
            .baseUrl(config.billetWeb.url).build()

        val factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient)).build()

        return factory.createClient(BilletWebClient::class.java)
    }
}