package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.use_cases.ports.MailPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}

@Service
class ParticipantNotif(
    private val config: BilhedBackConfig,
    private val participantPort: ParticipantPort,
    private val sendSms: SendSms,
    private val mailPort: MailPort,
) {

    fun notif(overrideNbNotif: Map<PassType, Int>) {
        val count = participantPort.getAlreadyDrawnCount()
        val nbToNotif = count.mapValues { (type, count) ->
            val nbPass = requireNotNull(config.passNumber[type]) { "Unable to notify without [$type] configuration" }
            val remaining = maxOf(nbPass - count, 0)
            overrideNbNotif[type]?.let { minOf(it, remaining) } ?: remaining
        }

        logger.info { "Notifying participants: $nbToNotif" }

        val participants = nbToNotif.mapValues { (type, count) ->
            participantPort.listTopDrawByPassWithLimit(type, count).also {
                if (it.size != count) {
                    logger.warn { "Asked to notify [$count] participants for [$type] but only [${it.size}] found in database" }
                }
            }
        }.values.flatten()

        participants.forEach {
            logger.info { "Notifying participant to confirm the ticket [${it.firstname} ${it.lastname}]" }
            val model = mapOf("firstname" to it.firstname, "lastname" to it.lastname, "year" to config.breizhCampYear.toString())
            val res = sendSms.sendSms(it).copy(mailConfirmSentDate = ZonedDateTime.now())

            mailPort.send(Mail(res.getMailAddress(), "draw_success", model))

            participantPort.save(res)
        }
    }

}