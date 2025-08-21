package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.GroupPort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.domain.use_cases.ports.RegistrationInfosPort
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
    private val registrationInfosPort: RegistrationInfosPort
) {

    private fun PersonRegister.toPerson(id: UUID = UUID.randomUUID(), groupId: UUID) = Person(
        id = id,
        lastname = this.lastname,
        firstname = this.firstname,
        status = PersonStatus.REGISTERED,
        telephone = this.telephone,
        email = this.email,
        groupId = groupId,
    )

    @Transactional
    fun register(ref: PersonRegister, groupPayment: Boolean, companions: List<PersonRegister>, pass: PassType): UUID {
        val referentId = UUID.randomUUID()
        val groupId = registerGroup(Group(id = UUID.randomUUID(), referentId = referentId, groupPayment = groupPayment, pass = pass))

        val members = listOf(ref.toPerson(id = referentId, groupId = groupId)) + companions.map { it.toPerson(groupId = groupId ) }
        registerMembers(referentId, members)

        return referentId
    }

    fun registerGroup(group: Group): UUID {
        if (config.registerCloseDate.isBefore(ZonedDateTime.now())) {
            throw IllegalArgumentException("Les inscriptions sont closes.")
        }
        if (groupPort.isReferent(group.referentId)) {
            throw IllegalArgumentException("Cette personne est déjà référente d'un autre groupe.")
        }

        logger.info { "Creating new group with referent ID [${group.referentId}]" }
        groupPort.save(group)
        logger.info { "New group created" }
        return group.id
    }

    fun registerMembers(refId: UUID, persons: List<Person>) {
        if (config.registerCloseDate.isBefore(ZonedDateTime.now())) {
            throw IllegalArgumentException("Les inscriptions sont closes.")
        }

        val ref = persons.find { it.id == refId } ?: throw IllegalArgumentException("Referent with id $refId not found.")
        for (person in persons) {
            if (personPort.existsEmailOrPhone(person.email, person.telephone)) {
                throw IllegalArgumentException("Une inscription avec cet email ou ce téléphone existe déjà.")
            }
            logger.info { "Creating new member [${person.lastname} ${person.firstname}] with email [${person.email}] with group [${person.groupId}]" }
            personPort.save(person)
            logger.info { "New member [${person.lastname} ${person.firstname}] created" }
        }
        logger.info { "Creating new referent info [${ref.lastname} ${ref.firstname}] with email [${ref.email}] for the group [${ref.groupId}]" }
        val infos = RegistrationInfos(ref.id)
        registrationInfosPort.save(infos)
        logger.info { "New referent infos for [${ref.lastname} ${ref.firstname}] created" }

        smsSend.sendSms(ref, infos)
    }

    fun changePhoneNumber(id: UUID, phone: String) {
        val ref = personPort.get(id)

        if (personPort.existsEmailOrPhone("", phone)) {
            logger.warn { "Trying to change phone number of registered [$id][${ref.lastname} ${ref.firstname}] to [$phone] but this phone number is already used" }
            throw IllegalArgumentException("Une inscription avec cet email ou ce téléphone existe déjà.")
        }

        logger.info { "Changing phone number of registered [${ref.lastname} ${ref.firstname}] to [$phone]" }
        val pers = ref.copy(telephone = phone)
        personPort.save(pers)

        smsSend.sendSms(pers, registrationInfosPort.get(id))
    }

    fun resendSms(id: UUID) {
        smsSend.sendSms(personPort.get(id), registrationInfosPort.get(id))
    }

    @Transactional
    fun saveSmsStatus(id: UUID, error: String?) {
        val smsStatus = if (error == null) SmsStatus.SENT else SmsStatus.ERROR
        registrationInfosPort.updateSms(id = id, smsStatus = smsStatus, error = error)
    }

    @Transactional(noRollbackFor = [IllegalArgumentException::class])
    fun validateToken(id: UUID, code: String) {
        if (!code.matches("^[0-9]{6}\$".toRegex())) throw IllegalArgumentException("Le code saisi est invalide")
        val regInfos = registrationInfosPort.get(id)

        if (regInfos.nbTokenTries >= 3) throw IllegalArgumentException("Vous avez dépassé le nombre de tentatives autorisées, merci de nous contacter")
        if (code != regInfos.token) {
            registrationInfosPort.save(regInfos.copy(nbTokenTries = regInfos.nbTokenTries + 1))
            throw IllegalArgumentException("Le code saisi est invalide")
        }

        levelUpAndNotify(id)
    }

    @Transactional
    fun levelUpAndNotify(referentId: UUID) {
        val persons = personPort.getMembersBy(referentId = referentId)
        persons.forEach {
            personPort.levelUpTo(it.id, PersonStatus.PARTICIPANT)
        }

        val ref = persons.find { it.id == referentId } ?: throw IllegalStateException("Referent with id $referentId not found.")
        val group = groupPort.get(ref.groupId)

        val model = mapOf("firstname" to ref.firstname, "lastname" to ref.lastname, "year" to config.breizhCampYear.toString(), "nbPersons" to persons.size.toString(), "groupPayment" to group.groupPayment.toString(), "pass" to group.pass.label)

        sendNotification.sendEmail(Mail(ref.getMailAddress(), "register", model, referentId), NotifOrigin.AUTOMATIC)

        logger.info { "Validated group of [${ref.lastname} ${ref.firstname}] as a participant" }
    }
}