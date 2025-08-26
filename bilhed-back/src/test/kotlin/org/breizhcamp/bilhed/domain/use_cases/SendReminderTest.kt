package org.breizhcamp.bilhed.domain.use_cases

import io.mockk.called
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

@ExtendWith(MockKExtension::class)
class SendReminderTest {
    @RelaxedMockK
    lateinit var configPort: ConfigPort

    @RelaxedMockK
    lateinit var notificationPort: NotificationPort

    @RelaxedMockK
    lateinit var reminderConfigPort: ReminderConfigPort

    @RelaxedMockK
    lateinit var registeredReminder: RegisteredReminder

    @RelaxedMockK
    lateinit var participantNotify: ParticipantNotify

    @RelaxedMockK
    lateinit var attendeeNotify: AttendeeNotify

    @RelaxedMockK
    lateinit var personPort: PersonPort

    @RelaxedMockK
    lateinit var registrationInfoPort: RegistrationInfoPort

    @RelaxedMockK
    lateinit var participationInfoPort: ParticipationInfoPort

    @RelaxedMockK
    lateinit var timeService: TimeService

    lateinit var sendReminder: SendReminder

    lateinit var person: Person

    lateinit var registrationInfo: RegistrationInfo

    lateinit var participationInfo: ParticipationInfo

    lateinit var reminderConfigsReg: List<ReminderConfig>

    lateinit var reminderConfigsPar: List<ReminderConfig>

    var now: ZonedDateTime = ZonedDateTime.now()

    @BeforeEach
    fun setUp() {
        /**
         *  Paramètres utilisés pour tester SendReminder :
         *      inscription : 9h
         *      tpsMaxInscription = 13h ; deadline = 22h
         *      config1 = 10 ; rappel 1 à deadline-10h = 12h
         *      config2 = 5 ; rappel 2 à deadline-5h = 17h
         *      config3 = 1 ; rappel 3 à deadline-1h = 21h
         */
        val refId = UUID.randomUUID()

        person = Person(
            id = refId,
            lastname = "Dupont",
            firstname = "Jean",
            status = PersonStatus.REGISTERED,
            telephone = "+33612345678",
            email = "jean.dupont@example.com",
            groupId = UUID.randomUUID(),
        )

        registrationInfo = RegistrationInfo(
            personId = refId,
            registrationDate = now.withHour(9).withMinute(0).withSecond(0)
        )

        participationInfo = ParticipationInfo(
            personId = refId,
            notificationConfirmSentDate = now.withHour(9).withMinute(30).withSecond(0)
        )

        reminderConfigsReg = listOf(
            ReminderConfig(UUID.randomUUID(), "REGISTERED", 10, "mail10", "sms10"),
            ReminderConfig(UUID.randomUUID(), "REGISTERED", 5, "mail5", "sms5"),
            ReminderConfig(UUID.randomUUID(), "REGISTERED", 1, "mail1", "sms1")
        )

        reminderConfigsPar = listOf(
            ReminderConfig(UUID.randomUUID(), "PARTICIPANT", 8, "mail8", "sms8"),
            ReminderConfig(UUID.randomUUID(), "PARTICIPANT", 2, "mail2", "sms2")
        )

        sendReminder = SendReminder(
            notificationPort = notificationPort,
            reminderConfigPort = reminderConfigPort,
            registeredReminder = registeredReminder,
            participantNotify = participantNotify,
            attendeeNotify = attendeeNotify,
            configPort = configPort,
            timeService = timeService,
            registrationInfoPort = registrationInfoPort,
            participationInfoPort = participationInfoPort,
            personPort = personPort
        )
    }

    // ------- Test for registered --------

    @Test
    fun `should send first reminder after registration`() {
        /**
         * Cas commun :
         * Inscription 9h - notif inscription 9h
         * Premier rappel à 12h (ou 12h01 dépendant des secondes)
         */
        val prevNotif = Notification(
            UUID.randomUUID(),
            now.withHour(9).withMinute(0).withSecond(10),
            "sms1",
            NotifMethod.SMS,
            person.id,
            emptyMap(),
            NotifOrigin.AUTOMATIC
        )

        every { configPort.get("reminderTimeReg") } returns Config("reminderTimeReg", "13")
        every { registrationInfoPort.list(PersonStatus.REGISTERED) } returns listOf(registrationInfo)
        every { reminderConfigPort.listBy("REGISTERED") } returns reminderConfigsReg
        every { notificationPort.findLatestReminderPerPerson(any()) } returns listOf(prevNotif)
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
        val prevNotif = Notification(
            UUID.randomUUID(),
            now.withHour(12).withMinute(0).withSecond(0),
            "sms1",
            NotifMethod.SMS,
            person.id,
            emptyMap(),
            NotifOrigin.AUTOMATIC
        )

        every { configPort.get("reminderTimeReg") } returns Config("reminderTimeReg", "13")
        every { registrationInfoPort.list(PersonStatus.REGISTERED) } returns listOf(registrationInfo)
        every { reminderConfigPort.listBy("REGISTERED") } returns reminderConfigsReg
        every { notificationPort.findLatestReminderPerPerson(any()) } returns listOf(prevNotif)
        every { timeService.now() } returns now.withHour(12).withMinute(2).withSecond(0)

        sendReminder.sendRegisteredReminder()

        verify { registeredReminder wasNot called }
    }

    @Test
    fun `should catching up after server shutdown`() {
        /**
         * Cas serveur arrêté et rattrapage :
         * Inscription 9h - notif inscription 9h
         * Arrêt du serveur à 15h
         * Rappel de 17h non envoyé
         * Redémarrage du serveur à 20h00
         * Prochain rappel à 21h, donc rappel de 17h rattrapé
         */
        val prevNotif = Notification(
            UUID.randomUUID(),
            now.withHour(12).withMinute(0).withSecond(0),
            "sms1",
            NotifMethod.SMS,
            person.id,
            emptyMap(),
            NotifOrigin.AUTOMATIC
        )

        every { configPort.get("reminderTimeReg") } returns Config("reminderTimeReg", "13")
        every { registrationInfoPort.list(PersonStatus.REGISTERED) } returns listOf(registrationInfo)
        every { reminderConfigPort.listBy("REGISTERED") } returns reminderConfigsReg
        every { notificationPort.findLatestReminderPerPerson(any()) } returns listOf(prevNotif)
        every { timeService.now() } returns now.withHour(20).withMinute(0).withSecond(0)

        sendReminder.sendRegisteredReminder()

        verify { registeredReminder.send(person.id, "sms5", "mail5", any()) }
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
        val prevNotif = Notification(
            UUID.randomUUID(),
            now.withHour(9).withMinute(0).withSecond(0),
            "sms1",
            NotifMethod.SMS,
            person.id,
            emptyMap(),
            NotifOrigin.AUTOMATIC
        )

        every { configPort.get("reminderTimeReg") } returns Config("reminderTimeReg", "13")
        every { registrationInfoPort.list(PersonStatus.REGISTERED) } returns listOf(registrationInfo)
        every { reminderConfigPort.listBy("REGISTERED") } returns reminderConfigsReg
        every { notificationPort.findLatestReminderPerPerson(any()) } returns listOf(prevNotif)
        every { timeService.now() } returns now.withHour(16).withMinute(30).withSecond(0)

        sendReminder.sendRegisteredReminder()

        verify { registeredReminder wasNot called }

    }

    @Test
    fun `should not send reminder because deadline passed` () {
        /**
         * Inscription 9h - notif inscription 9h
         * Arrêt du serveur de 10h à 22h
         * Date limite : 22h, pas d'envoi de rappel
         */
        every { configPort.get("reminderTimeReg") } returns Config("reminderTimeReg", "13")
        every { registrationInfoPort.list(PersonStatus.REGISTERED) } returns listOf(registrationInfo)
        every { reminderConfigPort.listBy("REGISTERED") } returns reminderConfigsReg
        every { timeService.now() } returns now.withHour(22).withMinute(1).withSecond(0)

        sendReminder.sendRegisteredReminder()

        verify { registeredReminder wasNot called }
    }

    // ------- Test for participant/attendee --------

    @Test
    fun `should send reminder after confirmation`() {
        /**
         * Cas commun :
         * Notif confirmation envoyée à 9h - limite 22h30
         * Premier rappel à 14h30
         */
        val prevNotif = Notification(
            UUID.randomUUID(),
            now.withHour(9).withMinute(0).withSecond(10),
            "sms1",
            NotifMethod.SMS,
            person.id,
            emptyMap(),
            NotifOrigin.AUTOMATIC
        )

        every { configPort.get("reminderTimePar") } returns Config("reminderTimePar", "13")
        every { participationInfoPort.list(PersonStatus.PARTICIPANT) } returns listOf(participationInfo)
        every { reminderConfigPort.listBy("PARTICIPANT") } returns reminderConfigsPar
        every { notificationPort.findLatestReminderPerPerson(any()) } returns listOf(prevNotif)
        every { timeService.now() } returns now.withHour(14).withMinute(31).withSecond(0)

        sendReminder.sendParticipantReminder()

        verify { participantNotify.remindSuccess(listOf(person.id), any(), "mail8") }
    }

    @Test
    fun `should catching up to participant after server shutdown`() {
        /**
         * Cas serveur arrêté et rattrapage :
         * Notif confirmation envoyée à 9h - limite 22h30
         * Arrêt du serveur à 13h
         * Redémarrage du serveur à 17h00
         * Prochain rappel à 20h30, donc rappel de 14h30 rattrapé
         */
        val prevNotif = Notification(
            UUID.randomUUID(),
            now.withHour(9).withMinute(0).withSecond(10),
            "sms1",
            NotifMethod.SMS,
            person.id,
            emptyMap(),
            NotifOrigin.AUTOMATIC
        )

        every { configPort.get("reminderTimePar") } returns Config("reminderTimePar", "13")
        every { participationInfoPort.list(PersonStatus.PARTICIPANT) } returns listOf(participationInfo)
        every { reminderConfigPort.listBy("PARTICIPANT") } returns reminderConfigsPar
        every { notificationPort.findLatestReminderPerPerson(any()) } returns listOf(prevNotif)
        every { timeService.now() } returns now.withHour(17).withMinute(0).withSecond(0)

        sendReminder.sendParticipantReminder()

        verify { participantNotify.remindSuccess(listOf(person.id), any(), "mail8") }
    }

}