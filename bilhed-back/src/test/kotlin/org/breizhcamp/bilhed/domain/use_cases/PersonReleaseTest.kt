package org.breizhcamp.bilhed.domain.use_cases

import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.breizhcamp.bilhed.domain.entities.Config
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.ConfigPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.breizhcamp.bilhed.infrastructure.TimeService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime
import java.util.*

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
    lateinit var participantPort: ParticipantPort

    @RelaxedMockK
    lateinit var attendeePort: AttendeePort

    @RelaxedMockK
    lateinit var timeService: TimeService

    @RelaxedMockK
    lateinit var ticketPort: TicketPort

    lateinit var participant: Participant

    lateinit var personRelease: PersonRelease

    var now: ZonedDateTime = ZonedDateTime.now()



    @BeforeEach
    fun setUp() {
        participant = Participant(
            UUID.randomUUID(),
            "Dupont",
            "Jean",
            "jean.dupont@example.com",
            "+33612345678",
            PassType.TWO_DAYS,
            null,
            now.withHour(9).withMinute(0).withSecond(0),
            null
        )

        personRelease = PersonRelease(
            attendeePort, participantPort, configPort, ticketPort, timeService
        )
    }

    @Test
    fun `should release participant`() {
        /**
         * Cas : deadline dépassée
         */
        every { timeService.now() } returns now.withHour(22).withMinute(1).withSecond(0)
        every { configPort.get("reminderTimePar") } returns Config("reminderTimePar", "13")
        every { participantPort.list() } returns listOf(participant)

        personRelease.participantReleaseAuto()

        verify { participantPort.levelUpToReleased(participant.id) }
    }

    @Test
    fun `should not release participant`() {
        /**
         * Cas : deadline pas dépassée
         */
        every { timeService.now() } returns now.withHour(21).withMinute(59).withSecond(0)
        every { configPort.get("reminderTimePar") } returns Config("reminderTimePar", "13")
        every { participantPort.list() } returns listOf(participant)

        personRelease.participantReleaseAuto()

        verify(exactly = 0) { participantPort.levelUpToReleased(any()) }
    }
}