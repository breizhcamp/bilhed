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
            notifySuccessParticipant(it, limitDate)
        }
    }

    /** Notify the list of [ids] that they have been drawn */
    fun notifySuccess(ids: List<UUID>) {
        val limitDate = getLimitDate()

        ids.forEach {
            val participant = participantPort.get(it)
            notifySuccessParticipant(participant, limitDate)
        }
    }

    private fun notifySuccessParticipant(p: Participant, limitDate: LimitDate) {
        logger.info { "Notifying success participant to confirm the ticket [${p.firstname} ${p.lastname}]" }
        val shortLink = urlShortenerPort.shorten(getConfirmSuccessLink(p), config.breizhCampCloseDate)
        val model = mapOf(
            "firstname" to p.firstname, "lastname" to p.lastname, "year" to config.breizhCampYear.toString(),
            "link" to shortLink, "limit_date" to limitDate.str, "delay" to limitDate.delayStr
        )

        val resSms = sendDrawSuccessSms(p, model)
        mailPort.send(Mail(p.getMailAddress(), "draw_success", model))

        participantPort.save(resSms.copy(mailConfirmSentDate = limitDate.now, confirmationLimitDate = limitDate.date))
    }

    fun notifyWaiting(ids: List<UUID>) = ids.forEach { notifyWaitingParticipant(it, "draw_waiting") }

    fun notifyFailed(ids: List<UUID>) = ids.forEach { notifyWaitingParticipant(it, "draw_failed") }
    fun remindSuccess(ids: List<UUID>) = ids.forEach {
        val p = participantPort.get(it)
        try {
            logger.info { "Reminding success participant to confirm the ticket [${p.firstname} ${p.lastname}]" }
            val limitDate = requireNotNull(p.confirmationLimitDate) { "Participant [${p.firstname} ${p.lastname}] has no confirmation limit date" }
            val model = mapOf("firstname" to p.firstname, "lastname" to p.lastname, "year" to config.breizhCampYear.toString(),
                "link" to getConfirmSuccessLink(p), "limit_date" to formatDate(limitDate))
            mailPort.send(Mail(p.getMailAddress(), "draw_success_reminder", model))

        } catch (e: Exception) {
            logger.warn(e) { "Unable to remind participant [${p.firstname} ${p.lastname}]" }
        }
    }

    private fun notifyWaitingParticipant(id: UUID, template: String) {
        val p = participantPort.get(id)
        logger.info { "Notifying [$template] participant [${p.firstname} ${p.lastname}]" }
        val model = mapOf("firstname" to p.firstname, "lastname" to p.lastname, "year" to config.breizhCampYear.toString(),
            )
        mailPort.send(Mail(p.getMailAddress(), template, model))
    }

    private fun getConfirmSuccessLink(p: Participant) = "${config.participantFrontUrl}/#/${p.id}/success"

    private fun getLimitDate(): LimitDate {
        val now = ZonedDateTime.now(ZoneId.of("Europe/Paris"))
        val limitDate = now.plusHours(72)
        val limitDateStr = formatDate(limitDate)
        return LimitDate(now, limitDate, limitDateStr, "72h")
    }

    private fun formatDate(limitDate: ZonedDateTime): String {
        val dateFormatter = DateTimeFormatter.ofPattern("eeee d MMMM à HH:mm", Locale.FRENCH)
        return dateFormatter.format(limitDate)
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