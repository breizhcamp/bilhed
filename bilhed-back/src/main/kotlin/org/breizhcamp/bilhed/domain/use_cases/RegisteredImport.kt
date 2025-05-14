package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class RegisteredImport(
    private val registeredPort: RegisteredPort,
    private val config: BilhedBackConfig,
    private val sendNotification: SendNotification
) {

    fun importCsv(file: InputStream) {
        val registered = CSVFormat.Builder.create().apply {
            setIgnoreSurroundingSpaces(true)
            setIgnoreEmptyLines(true)
        }.build().parse(file.reader()).drop(1).map {
            Registered(
                id = UUID.randomUUID(),
                lastname = it.get(0),
                firstname = it.get(1),
                email = it.get(2),
                telephone = it.get(3).keepOnlyDigits().toInternational(),
                pass = PassType.THREE_DAYS,
                kids = null,
            )
        }

        logger.info { "Importing [${registered.size}] registered" }

        registered.forEach {
            registeredPort.save(it)
        }
    }

    @Transactional
    fun levelUp(ids: List<UUID>) {
        logger.info { "Level up [${ids.size}] registered" }
        ids.forEach {
            registeredPort.levelUpToParticipant(it)

            val registered = registeredPort.get(it)
            val model = mapOf("firstname" to registered.firstname, "lastname" to registered.lastname, "year" to config.breizhCampYear.toString())
            sendNotification.sendEmail(Mail(registered.getMailAddress(), "register", model, it), ReminderOrigin.MANUAL)
        }
    }

}

private fun String.keepOnlyDigits(): String = this.filter { it.isDigit() }
private fun String.toInternational(): String = this.replaceFirst("0", "+33")
