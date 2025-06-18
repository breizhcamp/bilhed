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
    private val participationInfosPort: ParticipationInfosPort,
) {

    /** Notify the list of [ids] that they have been drawn */
    fun notifySuccess(ids: List<UUID>) {
        // the list contains ids of member if !groupPayment and id of referent else
        for (id in ids) {
            val participant = personPort.get(id)
            if (participationInfosPort.existsByPersonId(id)) continue // TODO que faire ? (quand c'est pas la première notif de succès)
            notifySuccessParticipant(participant)
        }
    }

    private fun notifySuccessParticipant(p: Person) {
        logger.info { "Notifying success participant to confirm the ticket [${p.firstname} ${p.lastname}]" }
        val partInfos = ParticipationInfos(p.id)
        val limitDate = getLimitDate(partInfos)

        val shortLink = urlShortenerPort.shorten(getConfirmSuccessLink(p), config.breizhCampCloseDate)
        val model = mapOf(
            "firstname" to p.firstname, "lastname" to p.lastname, "year" to config.breizhCampYear.toString(),
            "link" to shortLink, "limit_date" to limitDate.str, "delay" to limitDate.delayStr
        )


        val resSms = sendDrawSuccessSms(p, partInfos, model)
        sendNotification.sendEmail(Mail(p.getMailAddress(), "draw_success", model, p.id), ReminderOrigin.MANUAL)

        participationInfosPort.save(resSms.copy(notificationConfirmSentDate = limitDate.now))
    }

    fun notifyWaiting(ids: List<UUID>) = notifyWaitingParticipant(ids, "draw_waiting")

    fun notifyFailed(ids: List<UUID>) = notifyWaitingParticipant(ids, "draw_failed")

    fun remindSuccess(ids: List<UUID>, origin: ReminderOrigin, template: String = "draw_success_reminder") = ids.forEach {
        val p = personPort.get(it)
        val partInfos = participationInfosPort.get(it)

        try {
            logger.info { "Reminding success participant to confirm the ticket [${p.firstname} ${p.lastname}]" }
            val limitDate = requireNotNull(partInfos.notificationConfirmSentDate) { "Participant [${p.firstname} ${p.lastname}] has no notification confirmation limit date" }
            val model = mapOf("firstname" to p.firstname, "lastname" to p.lastname, "year" to config.breizhCampYear.toString(),
                "link" to getConfirmSuccessLink(p), "limit_date" to formatDate(limitDate))
            sendNotification.sendEmail(Mail(p.getMailAddress(), template, model, it), origin)

        } catch (e: Exception) {
            logger.warn(e) { "Unable to remind participant [${p.firstname} ${p.lastname}]" }
        }
    }

    private fun notifyWaitingParticipant(ids: List<UUID>, template: String) {
        val persons = personPort.get(ids)
        persons.forEach {
            logger.info { "Notifying [$template] participant [${it.firstname} ${it.lastname}]" }
            val model = mapOf("firstname" to it.firstname, "lastname" to it.lastname, "year" to config.breizhCampYear.toString(),
            )
            sendNotification.sendEmail(Mail(it.getMailAddress(), template, model, it.id), ReminderOrigin.MANUAL)
        }
    }

    private fun getConfirmSuccessLink(p: Person) = "${config.participantFrontUrl}/#/${p.id}/success"

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

    private fun sendDrawSuccessSms(p: Person, partInfos: ParticipationInfos, model: Map<String, String>): ParticipationInfos {
        val res = partInfos.copy(
            smsStatus = SmsStatus.SENDING,
            nbSmsSent = partInfos.nbSmsSent + 1,
            notificationConfirmSentDate = ZonedDateTime.now(),
        )

        sendNotification.sendSms(Sms(
            id = p.id,
            phone = p.telephone!!,
            template = "draw_success",
            model = model,
        ), ReminderOrigin.MANUAL)

        return res
    }

    @Transactional
    fun saveSmsStatus(id: UUID, error: String?) {
        val partInfos = participationInfosPort.get(id)
        val smsStatus = if (error == null) SmsStatus.SENT else SmsStatus.ERROR
        participationInfosPort.save(partInfos.copy(smsStatus = smsStatus, smsError = error))
    }

    private data class LimitDate(val now: ZonedDateTime, val date: ZonedDateTime, val str: String, val delayStr: String)
}