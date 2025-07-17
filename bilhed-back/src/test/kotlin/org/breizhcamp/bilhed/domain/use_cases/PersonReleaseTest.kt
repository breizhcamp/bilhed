package org.breizhcamp.bilhed.domain.use_cases

import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeeDataPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ConfigPort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.breizhcamp.bilhed.infrastructure.TimeService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime

/*
     Paramètres utilisés pour tester SendReminder :
     participation : 9h
     tpsMaxInscription = 13h ; deadline = 22h
 */
@ExtendWith(MockKExtension::class)
class PersonReleaseTest {

    @RelaxedMockK
    lateinit var configPort: ConfigPort

    @RelaxedMockK
    lateinit var personPort: PersonPort

    @RelaxedMockK
    lateinit var attendeeDataPort: AttendeeDataPort

    @RelaxedMockK
    lateinit var timeService: TimeService

    @RelaxedMockK
    lateinit var ticketPort: TicketPort

    lateinit var person: Person

    lateinit var releasePerson: ReleasePerson

    var now: ZonedDateTime = ZonedDateTime.now()



    @BeforeEach
    fun setUp() {
//        person = Person(
//            UUID.randomUUID(),
//            "Dupont",
//            "Jean",
//            PersonStatus.REGISTERED,
//            "jean.dupont@example.com",
//            "+33612345678",
//            PassType.TWO_DAYS,
//            null,
//            now.withHour(9).withMinute(0).withSecond(0),
//            null
//        )
//
//        personRelease = PersonRelease(
//            attendeeDataPort, personPort, configPort, ticketPort, timeService
//        )
    }

    @Test
    fun `should release participant`() {
        /**
         * Cas : deadline dépassée
         */
//        every { timeService.now() } returns now.withHour(22).withMinute(1).withSecond(0)
//        every { configPort.get("reminderTimePar") } returns Config("reminderTimePar", "13")
//        every { personPort.list() } returns listOf(person)
//
//        personRelease.participantReleaseAuto()
//
//        verify { personPort.levelUpToReleased(person.id) }
    }

    @Test
    fun `should not release participant`() {
        /**
         * Cas : deadline pas dépassée
         */
//        every { timeService.now() } returns now.withHour(21).withMinute(59).withSecond(0)
//        every { configPort.get("reminderTimePar") } returns Config("reminderTimePar", "13")
//        every { personPort.list() } returns listOf(person)
//
//        personRelease.participantReleaseAuto()
//
//        verify(exactly = 0) { personPort.levelUpToReleased(any()) }
    }
}