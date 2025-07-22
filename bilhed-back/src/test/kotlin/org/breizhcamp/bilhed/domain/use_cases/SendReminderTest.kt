package org.breizhcamp.bilhed.domain.use_cases

import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.*
import org.breizhcamp.bilhed.infrastructure.TimeService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime
import java.util.*

/*
     Paramètres utilisés pour tester SendReminder :
     inscription : 9h
     tpsMaxInscription = 13h ; deadline = 22h
     config1 = 10 ; rappel 1 à deadline-10h = 12h
     config2 = 5 ; rappel 2 à deadline-5h = 17h
     config3 = 1 ; rappel 3 à deadline-1h = 21h
 */
@ExtendWith(MockKExtension::class)
class SendReminderTest {
    @RelaxedMockK
    lateinit var configPort: ConfigPort

    @RelaxedMockK
    lateinit var reminderPort: ReminderPort

    @RelaxedMockK
    lateinit var reminderConfigPort: ReminderConfigPort

    @RelaxedMockK
    lateinit var registeredReminder: RegisteredReminder

    @RelaxedMockK
    lateinit var participantNotify: ParticipantNotify

    @RelaxedMockK
    lateinit var attendeeNotify: AttendeeNotify

    @RelaxedMockK
    lateinit var attendeeDataPort: AttendeeDataPort

    @RelaxedMockK
    lateinit var personPort: PersonPort

    @RelaxedMockK
    lateinit var timeService: TimeService

    lateinit var sendReminder: SendReminder

    lateinit var person: Person

    lateinit var reminderConfigs: List<ReminderConfig>

    var now: ZonedDateTime = ZonedDateTime.now()

    @BeforeEach
    fun setUp() {
//        person = Person(
//            UUID.randomUUID(),
//            "Dupont",
//            "Jean",
//            "jean.dupont@example.com",
//            "+33612345678",
//            PassType.TWO_DAYS,
//            null,
//            now.withHour(9).withMinute(0).withSecond(0),
//        )

        reminderConfigs = listOf(
            ReminderConfig(UUID.randomUUID(), "REGISTERED", 10, "mail10", "sms10"),
            ReminderConfig(UUID.randomUUID(), "REGISTERED", 5, "mail5", "sms5"),
            ReminderConfig(UUID.randomUUID(), "REGISTERED", 1, "mail1", "sms1")
        )
//
//        sendReminder = SendReminder(
//            personPort, reminderPort, reminderConfigPort, participantPort,
//            registeredReminder, participantNotif, attendeeNotify, attendeeDataPort, configPort, timeService, personPort
//        )
    }

    @Test
    fun `should send first reminder after registration`() {
        /**
         * Cas commun :
         * Inscription 9h - notif inscription 9h
         * Premier rappel à 12h (ou 12h01 dépendant des secondes)
         */
        val prevNotif = Reminder(
            UUID.randomUUID(),
            now.withHour(9).withMinute(0).withSecond(0),
            "sms1",
            ReminderMethod.SMS,
            person.id,
            emptyMap(),
            ReminderOrigin.AUTOMATIC
        )

        every { configPort.get("reminderTimeReg") } returns Config("reminderTimeReg", "13")
//        every { personPort.list() } returns listOf(person)
        every { reminderConfigPort.listByType("REGISTERED") } returns reminderConfigs
        every { reminderPort.findLatestReminderPerPerson(any()) } returns listOf(prevNotif)
        every { timeService.now() } returns now.withHour(12).withMinute(1).withSecond(0)

        sendReminder.sendRegisteredReminder()

        verify { registeredReminder.send(person.id, "sms10", "mail10", any()) }
    }

    @Test
    fun `should not send reminder`() {
        /**
         * Case commun :
         * Inscription 9h - notif inscription 9h
         * Premier rappel à 12h (ou 12h01 dépendant des secondes)
         * Jusqu'au prochain rappel, ne fait rien (de 12h01 à 17h)
         */
        val prevNotif = Reminder(
            UUID.randomUUID(),
            now.withHour(12).withMinute(0).withSecond(0),
            "sms1",
            ReminderMethod.SMS,
            person.id,
            emptyMap(),
            ReminderOrigin.AUTOMATIC
        )

        every { configPort.get("reminderTimeReg") } returns Config("reminderTimeReg", "13")
//        every { personPort.list() } returns listOf(person)
        every { reminderConfigPort.listByType("REGISTERED") } returns reminderConfigs
        every { reminderPort.findLatestReminderPerPerson(any()) } returns listOf(prevNotif)
        every { timeService.now() } returns now.withHour(12).withMinute(2).withSecond(0)

        sendReminder.sendRegisteredReminder()

//        verify { registeredReminder wasNot called }
    }

    @Test
    fun `should catching up after server shutdown`() {
        /**
         * Cas serveur arrêté et rattrapage :
         * Inscription 9h - notif inscription 9h
         * Arrêt du serveur à 15h
         * Rappel de 17h non envoyé
         * Redémarrage du serveur à 20h05
         * Prochain rappel à 21h, donc rappel de 17h rattrapé
         */
        val prevNotif = Reminder(
            UUID.randomUUID(),
            now.withHour(12).withMinute(0).withSecond(0),
            "sms1",
            ReminderMethod.SMS,
            person.id,
            emptyMap(),
            ReminderOrigin.AUTOMATIC
        )

        every { configPort.get("reminderTimeReg") } returns Config("reminderTimeReg", "13")
//        every { personPort.list() } returns listOf(person)
        every { reminderConfigPort.listByType("REGISTERED") } returns reminderConfigs
        every { reminderPort.findLatestReminderPerPerson(any()) } returns listOf(prevNotif)
        every { timeService.now() } returns now.withHour(20).withMinute(5).withSecond(0)

        sendReminder.sendRegisteredReminder()

//        verify { registeredReminder.send(person.id, "sms5", "mail5", any()) }
    }

    @Test
    fun `should not send reminder because another in less than 1 hour` () {
        /**
         * Cas serveur arrêté et pas de rattrapage :
         * Inscription 9h - notif inscription 9h
         * Arrêt du serveur à 11h
         * Rappel de 12h non envoyé
         * Redémarrage du serveur à 16h30
         * Prochain rappel à 17h, donc rappel de 12h non rattrapé
         */
        val prevNotif = Reminder(
            UUID.randomUUID(),
            now.withHour(9).withMinute(0).withSecond(0),
            "sms1",
            ReminderMethod.SMS,
            person.id,
            emptyMap(),
            ReminderOrigin.AUTOMATIC
        )

        every { configPort.get("reminderTimeReg") } returns Config("reminderTimeReg", "13")
//        every { personPort.list() } returns listOf(person)
        every { reminderConfigPort.listByType("REGISTERED") } returns reminderConfigs
        every { reminderPort.findLatestReminderPerPerson(any()) } returns listOf(prevNotif)
        every { timeService.now() } returns now.withHour(16).withMinute(30).withSecond(0)

        sendReminder.sendRegisteredReminder()

//        verify { registeredReminder wasNot called }

    }

    @Test
    fun `should not send reminder because deadline passed` () {
        /**
         * Inscription 9h - notif inscription 9h
         * Arrêt du serveur de 10h à 22h
         * Date limite : 22h, pas d'envoi de rappel
         */
        every { configPort.get("reminderTimeReg") } returns Config("reminderTimeReg", "13")
//        every { personPort.list() } returns listOf(person)
        every { reminderConfigPort.listByType("REGISTERED") } returns reminderConfigs
        every { timeService.now() } returns now.withHour(22).withMinute(1).withSecond(0)

        sendReminder.sendRegisteredReminder()

//        verify { registeredReminder wasNot called }
    }

}