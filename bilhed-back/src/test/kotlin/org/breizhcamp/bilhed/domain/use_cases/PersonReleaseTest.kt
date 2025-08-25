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
    lateinit var groupPort: GroupPort

    @RelaxedMockK
    lateinit var participationInfoPort: ParticipationInfoPort

    @RelaxedMockK
    lateinit var timeService: TimeService

    @RelaxedMockK
    lateinit var ticketPort: TicketPort

    lateinit var person: Person

    lateinit var group: Group

    lateinit var partInfo: ParticipationInfo

    lateinit var releasePerson: ReleasePerson

    var now: ZonedDateTime = ZonedDateTime.now()

    @BeforeEach
    fun setUp() {
        val refId = UUID.randomUUID()
        val groupId = UUID.randomUUID()

        group = Group(
            id = groupId,
            referentId = refId,
            pass = PassType.TWO_DAYS,
            groupPayment = true,
            drawOrder = 0
        )

        person = Person(
            id = refId,
            lastname = "Dupont",
            firstname = "Jean",
            status = PersonStatus.PARTICIPANT,
            telephone = "+33612345678",
            email = "jean.dupont@example.com",
            groupId = UUID.randomUUID(),
        )

        partInfo = ParticipationInfo(
            personId = refId,
            notificationConfirmSentDate = now.withHour(9).withMinute(0).withSecond(0)
        )

        releasePerson = ReleasePerson(
            personPort = personPort,
            configPort = configPort,
            ticketPort = ticketPort,
            timeService = timeService,
            groupPort = groupPort,
            partInfosPort = participationInfoPort,
        )
    }

    @Test
    fun `should release participant`() {
        /**
         * Cas : deadline dépassée
         */
        every { timeService.now() } returns now.withHour(22).withMinute(1).withSecond(0)
        every { configPort.get("reminderTimePar") } returns Config("reminderTimePar", "13")
        every { personPort.get(any<List<UUID>>()) } returns listOf(person)
        every { participationInfoPort.list(PersonStatus.PARTICIPANT) } returns listOf(partInfo)
        every { groupPort.extendedGroupBy(person.groupId) } returns Pair(group, listOf(person))

        releasePerson.participantReleaseAuto()

        verify { personPort.levelUpTo(person.id, PersonStatus.RELEASED) }
    }

    @Test
    fun `should not release participant`() {
        /**
         * Cas : deadline pas dépassée
         */
        every { timeService.now() } returns now.withHour(21).withMinute(59).withSecond(0)
        every { configPort.get("reminderTimePar") } returns Config("reminderTimePar", "13")
        every { personPort.get(any<List<UUID>>()) } returns listOf(person)
        every { participationInfoPort.list(PersonStatus.PARTICIPANT) } returns listOf(partInfo)
        every { groupPort.extendedGroupBy(person.groupId) } returns Pair(group, listOf(person))

        releasePerson.participantReleaseAuto()

        verify(exactly = 0) { personPort.levelUpTo(person.id, PersonStatus.RELEASED) }
    }
}