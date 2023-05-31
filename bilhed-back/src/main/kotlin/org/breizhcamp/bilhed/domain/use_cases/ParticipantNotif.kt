package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.MailPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.breizhcamp.bilhed.domain.use_cases.ports.SmsPort
import org.breizhcamp.bilhed.domain.use_cases.ports.UrlShortenerPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class ParticipantNotif(
    private val config: BilhedBackConfig,
    private val participantPort: ParticipantPort,
    private val smsPort: SmsPort,
    private val mailPort: MailPort,
    private val urlShortenerPort: UrlShortenerPort,
) {

    /** Notify the list of [ids] that they have been drawn */
    fun notifySuccess(ids: List<UUID>) {
        val limitDate = getLimitDate()

        ids.forEach {
            val participant = participantPort.get(it)
            notifyParticipant(participant, limitDate)
        }
    }

    fun notify(overrideNbNotif: Map<PassType, Int>) {
        val count = participantPort.getAlreadyNotifCount()
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

        val limitDate = getLimitDate()

        participants.forEach {
            notifyParticipant(it, limitDate)
        }
    }

    private fun notifyParticipant(p: Participant, limitDate: LimitDate) {
        logger.info { "Notifying participant to confirm the ticket [${p.firstname} ${p.lastname}]" }
        val link = "${config.participantFrontUrl}/#/${p.id}/success"
        val shortLink = urlShortenerPort.shorten(link, config.breizhCampCloseDate)
        val model = mapOf(
            "firstname" to p.firstname, "lastname" to p.lastname, "year" to config.breizhCampYear.toString(),
            "link" to shortLink, "limit_date" to limitDate.str, "delay" to limitDate.delayStr
        )

        val resSms = sendDrawSuccessSms(p, model)
        mailPort.send(Mail(p.getMailAddress(), "draw_success", model))

        participantPort.save(resSms.copy(mailConfirmSentDate = limitDate.now, confirmationLimitDate = limitDate.date))
    }

    private fun getLimitDate(): LimitDate {
        val now = ZonedDateTime.now(ZoneId.of("Europe/Paris"))
        val dateFormatter = DateTimeFormatter.ofPattern("eeee d MMMM à HH:mm", Locale.FRENCH)
        val limitDate = now.plusDays(7)
        val limitDateStr = dateFormatter.format(limitDate)
        return LimitDate(now, limitDate, limitDateStr, "7j")
    }

    private fun sendDrawSuccessSms(participant: Participant, model: Map<String, String>): Participant {
        val res = participant.copy(
            smsStatus = SmsStatus.SENDING,
            nbSmsSent = participant.nbSmsSent + 1,
            smsConfirmSentDate = ZonedDateTime.now(),
        )

        smsPort.send(Sms(
            id = participant.id,
            phone = participant.telephone,
            template = "draw_success",
            model = model,
        ))
        return res
    }

    @Transactional
    fun saveSmsStatus(id: UUID, error: String?) {
        val participant = participantPort.get(id)
        val smsStatus = if (error == null) SmsStatus.SENT else SmsStatus.ERROR
        participantPort.save(participant.copy(smsStatus = smsStatus, smsError = error))
    }

    private data class LimitDate(val now: ZonedDateTime, val date: ZonedDateTime, val str: String, val delayStr: String)
}