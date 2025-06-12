package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.GroupPort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ReferentInfosPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Register a new participant to the lottery
 */
@Service
class Registration(
    private val config: BilhedBackConfig,
    private val smsSend: SendRegistrationSms,
    private val sendNotification: SendNotification,
    private val groupPort: GroupPort,
    private val personPort: PersonPort,
    private val referentInfosPort: ReferentInfosPort
) {

    fun registerGroup(group: Group): Group {
        if (config.registerCloseDate.isBefore(ZonedDateTime.now())) {
            throw IllegalArgumentException("Les inscriptions sont closes.")
        }
        if (groupPort.isReferent(group.referentId)) {
            throw IllegalArgumentException("Cette personne est déjà référente d'un autre groupe.")
        }

        logger.info { "Creating new group with referent ID [${group.referentId}]" }
        groupPort.save(group)
        logger.info { "New group created" }
        return group
    }

    fun registerMembers(persons: List<Person>) {
        if (config.registerCloseDate.isBefore(ZonedDateTime.now())) {
            throw IllegalArgumentException("Les inscriptions sont closes.")
        }

        val ref = persons.first()
        for (person in persons) {
            if (personPort.existsEmailOrPhone(person.email, person.telephone)) {
                throw IllegalArgumentException("Une inscription avec cet email ou ce téléphone existe déjà.")
            }
            logger.info { "Creating new member [${person.lastname} ${person.firstname}] with email [${person.email}] with group [${person.groupId}]" }
            personPort.save(person)
            logger.info { "New member [${person.lastname} ${person.firstname}] created" }
        }
        logger.info { "Creating new referent info [${ref.lastname} ${ref.firstname}] with email [${ref.email}] for the group [${ref.groupId}]" }
        val infos = ReferentInfos(ref.id)
        referentInfosPort.save(infos)
        logger.info { "New referent infos for [${ref.lastname} ${ref.firstname}] created" }

        smsSend.sendSms(Referent(ref, infos))
    }

    fun getReferent(id: UUID): Referent {
        return Referent(
            person = personPort.get(id),
            referentInfos = referentInfosPort.get(id)
        )
    }

    fun changePhoneNumber(id: UUID, phone: String) {
        val referent = getReferent(id)

        if (personPort.existsEmailOrPhone("", phone)) {
            logger.warn { "Trying to change phone number of registered [$id][${referent.person.lastname} ${referent.person.firstname}] to [$phone] but this phone number is already used" }
            throw IllegalArgumentException("Une inscription avec cet email ou ce téléphone existe déjà.")
        }

        logger.info { "Changing phone number of registered [${referent.person.lastname} ${referent.person.firstname}] to [$phone]" }
        val pers = referent.person.copy(telephone = phone)
        val newRef = referent.copy(person = pers)
        personPort.save(pers)

        smsSend.sendSms(newRef)
    }

    fun resendSms(id: UUID) {
        smsSend.sendSms(getReferent(id))
    }

    @Transactional
    fun saveSmsStatus(id: UUID, error: String?) {
        val referent = getReferent(id)
        val smsStatus = if (error == null) SmsStatus.SENT else SmsStatus.ERROR
        referentInfosPort.save(referent.referentInfos.copy(smsStatus = smsStatus, smsError = error))
    }

    @Transactional(noRollbackFor = [IllegalArgumentException::class])
    fun validateToken(id: UUID, code: String) {
        if (!code.matches("^[0-9]{6}\$".toRegex())) throw IllegalArgumentException("Le code saisi est invalide")
        val referent = getReferent(id)
        if (referent.referentInfos.nbTokenTries >= 3) throw IllegalArgumentException("Vous avez dépassé le nombre de tentatives autorisées, merci de nous contacter")
        if (code != referent.referentInfos.token) {
            referentInfosPort.save(referent.referentInfos.copy(nbTokenTries = referent.referentInfos.nbTokenTries + 1))
            throw IllegalArgumentException("Le code saisi est invalide")
        }

        personPort.levelUpToParticipant(referent.person.id)
        personPort.getCompanions(referent.person.groupId, referent.person.id).forEach {
            personPort.levelUpToParticipant(it.id)
        }

        val model = mapOf("firstname" to referent.person.firstname, "lastname" to referent.person.lastname, "year" to config.breizhCampYear.toString())

        sendNotification.sendEmail(Mail(referent.person.getMailAddress(), "register", model, id), ReminderOrigin.AUTOMATIC)

        logger.info { "Validated group of [${referent.person.lastname} ${referent.person.firstname}] as a participant" }
    }
}