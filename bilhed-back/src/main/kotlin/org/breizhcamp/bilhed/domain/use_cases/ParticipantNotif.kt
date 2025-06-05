package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.ConfigPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipationInfosPort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
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
    private val personPort: PersonPort,
    private val urlShortenerPort: UrlShortenerPort,
    private val configPort: ConfigPort,
    private val sendNotification: SendNotification,
    private val participationInfosPort: ParticipationInfosPort
) {

    fun notify(overrideNbNotif: Map<PassType, Int>) {
        val count = personPort.getAlreadyNotifCount()
        val nbToNotif = count.mapValues { (type, count) ->
            val nbPass = requireNotNull(config.passNumber[type]) { "Unable to notify without [$type] configuration" }
            val remaining = maxOf(nbPass - count, 0)
            overrideNbNotif[type]?.let { minOf(it, remaining) } ?: remaining
        }

        logger.info { "Notifying participants: $nbToNotif" }

        val participants = nbToNotif.mapValues { (type, count) ->
            personPort.listTopDrawByPassWithLimit(type, count).also {
                if (it.size != count) {
                    logger.warn { "Asked to notify [$count] participants for [$type] but only [${it.size}] found in database" }
                }
            }
        }.values.flatten()

        participants.forEach {
            val participationInfos = participationInfosPort.get(it.id)
            val limitDate = getLimitDate(participationInfos)
            notifySuccessParticipant(Participation(it, participationInfos), limitDate)
        }
    }

    /** Notify the list of [ids] that they have been drawn */
    fun notifySuccess(ids: List<UUID>) {

        ids.forEach {
            val participant = personPort.get(it)
            val participationInfos = participationInfosPort.get(it)
            val limitDate = getLimitDate(participationInfos)
            notifySuccessParticipant(Participation(participant, participationInfos), limitDate)
        }
    }

    private fun notifySuccessParticipant(p: Participation, limitDate: LimitDate) {
        logger.info { "Notifying success participant to confirm the ticket [${p.person.firstname} ${p.person.lastname}]" }
        val shortLink = urlShortenerPort.shorten(getConfirmSuccessLink(p), config.breizhCampCloseDate)
        val model = mapOf(
            "firstname" to p.person.firstname, "lastname" to p.person.lastname, "year" to config.breizhCampYear.toString(),
            "link" to shortLink, "limit_date" to limitDate.str, "delay" to limitDate.delayStr
        )

        val resSms = sendDrawSuccessSms(p, model)
        sendNotification.sendEmail(Mail(p.person.getMailAddress(), "draw_success", model, p.person.id), ReminderOrigin.MANUAL)

        participationInfosPort.save(resSms.copy(notificationConfirmSentDate = limitDate.now))
    }

    fun notifyWaiting(ids: List<UUID>) = ids.forEach { notifyWaitingParticipant(it, "draw_waiting") }

    fun notifyFailed(ids: List<UUID>) = ids.forEach { notifyWaitingParticipant(it, "draw_failed") }
    fun remindSuccess(ids: List<UUID>, origin: ReminderOrigin, template: String = "draw_success_reminder") = ids.forEach {
        val p = Participation(
            personPort.get(it),
            participationInfosPort.get(it)
        )
        try {
            logger.info { "Reminding success participant to confirm the ticket [${p.person.firstname} ${p.person.lastname}]" }
            val limitDate = requireNotNull(p.participationInfos.notificationConfirmSentDate) { "Participant [${p.person.firstname} ${p.person.lastname}] has no notification confirmation limit date" }
            val model = mapOf("firstname" to p.person.firstname, "lastname" to p.person.lastname, "year" to config.breizhCampYear.toString(),
                "link" to getConfirmSuccessLink(p), "limit_date" to formatDate(limitDate))
            sendNotification.sendEmail(Mail(p.person.getMailAddress(), template, model, it), origin)

        } catch (e: Exception) {
            logger.warn(e) { "Unable to remind participant [${p.person.firstname} ${p.person.lastname}]" }
        }
    }

    private fun notifyWaitingParticipant(id: UUID, template: String) {
        val p = personPort.get(id)
        logger.info { "Notifying [$template] participant [${p.firstname} ${p.lastname}]" }
        val model = mapOf("firstname" to p.firstname, "lastname" to p.lastname, "year" to config.breizhCampYear.toString(),
            )
        sendNotification.sendEmail(Mail(p.getMailAddress(), template, model, id), ReminderOrigin.MANUAL)
    }

    private fun getConfirmSuccessLink(p: Participation) = "${config.participantFrontUrl}/#/${p.person.id}/success"

    private fun getLimitDate(p: ParticipationInfos): LimitDate {
        val notifDate = p.notificationConfirmSentDate ?: ZonedDateTime.now(ZoneId.of("Europe/Paris"))
        val timeLimit = configPort.get("reminderTimePar")
        val limitDate = notifDate.plusHours(timeLimit.value.toLong())
        val limitDateStr = formatDate(limitDate)
        return LimitDate(notifDate, limitDate, limitDateStr, "${timeLimit.value}h")
    }

    private fun formatDate(limitDate: ZonedDateTime): String {
        val dateFormatter = DateTimeFormatter.ofPattern("eeee d MMMM à HH:mm", Locale.FRENCH)
        return dateFormatter.format(limitDate)
    }

    private fun sendDrawSuccessSms(participation: Participation, model: Map<String, String>): ParticipationInfos {
        val res = participation.participationInfos.copy(
            smsStatus = SmsStatus.SENDING,
            nbSmsSent = participation.participationInfos.nbSmsSent + 1,
            notificationConfirmSentDate = ZonedDateTime.now(),
        )

        sendNotification.sendSms(Sms(
            id = participation.person.id,
            phone = participation.person.telephone!!,
            template = "draw_success",
            model = model,
        ), ReminderOrigin.MANUAL)

        return res
    }

    @Transactional
    fun saveSmsStatus(id: UUID, error: String?) {
        val participant = Participation(
            personPort.get(id),
            participationInfosPort.get(id)
        )
        val smsStatus = if (error == null) SmsStatus.SENT else SmsStatus.ERROR
        participationInfosPort.save(participant.participationInfos.copy(smsStatus = smsStatus, smsError = error))
    }

    private data class LimitDate(val now: ZonedDateTime, val date: ZonedDateTime, val str: String, val delayStr: String)
}