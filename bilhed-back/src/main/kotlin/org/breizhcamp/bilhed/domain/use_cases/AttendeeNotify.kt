package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.MailPort
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class AttendeeNotify(
    private val config: BilhedBackConfig,
    private val attendeePort: AttendeePort,
    private val mailPort: MailPort,
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

    private fun getConfirmSuccessLink(p: Person) = "${config.participantFrontUrl}/#/${p.id}/success"

    private fun formatDate(limitDate: ZonedDateTime): String {
        val dateFormatter = DateTimeFormatter.ofPattern("eeee d MMMM à HH:mm", Locale.FRENCH)
        return dateFormatter.format(limitDate)
    }
}