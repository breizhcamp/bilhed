package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class RegisteredImport(
    private val personPort: PersonPort,
    private val config: BilhedBackConfig,
    private val sendNotification: SendNotification
) {

    @Transactional
    fun levelUp(ids: List<UUID>) {
        logger.info { "Level up [${ids.size}] registered" }
        ids.forEach {
            personPort.levelUpToParticipant(it)

            val registered = personPort.get(it)
            val model = mapOf("firstname" to registered.firstname, "lastname" to registered.lastname, "year" to config.breizhCampYear.toString())
            sendNotification.sendEmail(Mail(registered.getMailAddress(), "register", model, it), ReminderOrigin.MANUAL)
        }
    }

}

private fun String.keepOnlyDigits(): String = this.filter { it.isDigit() }
private fun String.toInternational(): String = this.replaceFirst("0", "+33")
