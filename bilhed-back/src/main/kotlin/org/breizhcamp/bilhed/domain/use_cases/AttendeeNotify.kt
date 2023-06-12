package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.Sms
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.MailPort
import org.breizhcamp.bilhed.domain.use_cases.ports.SmsPort
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
    private val attendeePort: AttendeePort,
    private val mailPort: MailPort,
    private val smsPort: SmsPort,
    private val urlShortenerPort: UrlShortenerPort,
) {

    fun remindPayedMail(ids: List<UUID>) = ids.forEach {
        val a = attendeePort.get(it)

        try {
            logger.info { "Sending remind payed mail to [${a.firstname} ${a.lastname}]" }

            val model = mapOf("firstname" to a.firstname, "lastname" to a.lastname, "year" to config.breizhCampYear.toString(),
                "link" to getConfirmSuccessLink(a), "limit_date" to formatDate(a.confirmationLimitDate))
            mailPort.send(Mail(a.getMailAddress(), "payed_reminder", model))

        } catch (e: Exception) {
            logger.error(e) { "Unable to send remind payed mail to [${a.firstname} ${a.lastname}]" }
        }
    }

    fun remindPayedSms(ids: List<UUID>) = ids.forEach {
        val a = attendeePort.get(it)

        try {
            logger.info { "Sending remind payed SMS to [${a.firstname} ${a.lastname}]" }
            val remainingTime = getRemainingTime(a.confirmationLimitDate) ?: run {
                logger.warn { "Not sending reminder to [${a.firstname} ${a.lastname}] because confirm time is over" }
                return@forEach
            }

            val shortLink = urlShortenerPort.shorten(getConfirmSuccessLink(a), config.breizhCampCloseDate)

            val model = mapOf("firstname" to a.firstname, "lastname" to a.lastname, "year" to config.breizhCampYear.toString(),
                "link" to shortLink, "limit_time" to formatLimitTime(a.confirmationLimitDate),
                "remaining_time" to remainingTime)

            smsPort.send(Sms(a.telephone, "payed_reminder", model))

        } catch (e: Exception) {
            logger.error(e) { "Unable to send remind payed sms to [${a.firstname} ${a.lastname}]" }
        }
    }

    private fun getConfirmSuccessLink(p: Person) = "${config.participantFrontUrl}/#/${p.id}/success"

    private fun formatDate(limitDate: ZonedDateTime): String {
        return DateTimeFormatter.ofPattern("eeee d MMMM à HH:mm", Locale.FRENCH).format(limitDate)
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