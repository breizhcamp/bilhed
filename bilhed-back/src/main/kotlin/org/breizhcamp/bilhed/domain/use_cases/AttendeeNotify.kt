package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.entities.Sms
import org.breizhcamp.bilhed.domain.use_cases.ports.ConfigPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipationInfosPort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.domain.use_cases.ports.UrlShortenerPort
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class AttendeeNotify(
    private val config: BilhedBackConfig,
    private val urlShortenerPort: UrlShortenerPort,
    private val sendNotification: SendNotification,
    private val configPort: ConfigPort,
    private val personPort: PersonPort,
    private val participationInfosPort: ParticipationInfosPort
) {

    fun remindPayedMail(ids: List<UUID>, origin: ReminderOrigin, template: String = "payed_reminder") {
        val persons = personPort.get(ids)
        val partInfos = participationInfosPort.get(ids).associateBy { it.personId }

        for (p in persons) {
            val notifConfirmDate = partInfos[p.id]?.confirmationDate ?: throw IllegalArgumentException("No confirmation date found")

            try {
                logger.info { "Sending remind payed mail to [${p.firstname} ${p.lastname}]" }

                val model = mapOf("firstname" to p.firstname, "lastname" to p.lastname, "year" to config.breizhCampYear.toString(),
                    "link" to getConfirmSuccessLink(p), "limit_date" to formatDate(getLimitDate(notifConfirmDate)))

                sendNotification.sendEmail(Mail(p.getMailAddress(), template, model, p.id), origin)

            } catch (e: Exception) {
                logger.error(e) { "Unable to send remind payed mail to [${p.firstname} ${p.lastname}]" }
            }
        }
    }

    fun remindPayedSms(ids: List<UUID>, origin: ReminderOrigin, template: String = "payed_reminder") {
        val persons = personPort.get(ids)
        val partInfos = participationInfosPort.get(ids).associateBy { it.personId }

        for (p in persons) {
            val notifConfirmDate = partInfos[p.id]?.notificationConfirmSentDate ?: throw IllegalStateException("No confirmation date found")

            try {
                logger.info { "Sending remind payed SMS to [${p.firstname} ${p.lastname}]" }
                val remainingTime = getRemainingTime(getLimitDate(notifConfirmDate)) ?: run {
                    logger.warn { "Not sending reminder to [${p.firstname} ${p.lastname}] because confirm time is over" }
                    return
                }

                val shortLink = urlShortenerPort.shorten(getConfirmSuccessLink(p), config.breizhCampCloseDate)

                val model = mapOf("firstname" to p.firstname, "lastname" to p.lastname, "year" to config.breizhCampYear.toString(),
                    "link" to shortLink, "limit_time" to formatLimitTime(getLimitDate(notifConfirmDate)),
                    "remaining_time" to remainingTime)

                if (p.telephone == null) throw IllegalStateException("Telephone must not be null for person [${p.id}] / [${p.firstname} ${p.lastname}]")
                sendNotification.sendSms(Sms(p.telephone, template, model), origin)


            } catch (e: Exception) {
                logger.error(e) { "Unable to send remind payed sms to [${p.firstname} ${p.lastname}]" }
            }
        }
    }

    private fun getConfirmSuccessLink(p: Person) = "${config.participantFrontUrl}/#/${p.id}/success"

    private fun formatDate(limitDate: ZonedDateTime): String {
        return DateTimeFormatter.ofPattern("eeee d MMMM à HH:mm", Locale.FRENCH).format(limitDate)
    }

    private fun getLimitDate(notifConfirmDate: ZonedDateTime): ZonedDateTime {
        val reminderTimeAtt = configPort.get("reminderTimeAtt").value.toLong()
        return notifConfirmDate.plusHours(reminderTimeAtt)
    }

    private fun getRemainingTime(limitDate: ZonedDateTime): String? {
        val duration = Duration.between(ZonedDateTime.now(), limitDate)
        if (duration.isNegative) return null
        if (duration.toDays() > 3) return duration.toDays().toString() + "j"
        if (duration.toHours() > 0) return duration.toHours().toString() + "h"
        return duration.toMinutes().toString() + "m"
    }

    private fun formatLimitTime(limitDate: ZonedDateTime): String {
        val days = Duration.between(ZonedDateTime.now(), limitDate).toDays()
        val pattern = if (days > 0) "eee HH:mm" else "HH:mm"
        return DateTimeFormatter.ofPattern(pattern, Locale.FRENCH).format(limitDate.withZoneSameInstant(ZoneId.of("Europe/Paris")))
    }
}