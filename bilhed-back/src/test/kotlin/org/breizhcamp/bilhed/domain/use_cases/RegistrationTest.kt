package org.breizhcamp.bilhed.domain.use_cases

import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.domain.entities.PersonRegister
import org.breizhcamp.bilhed.domain.use_cases.ports.GroupPort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.domain.use_cases.ports.RegistrationInfoPort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RegistrationTest {

    @RelaxedMockK
    lateinit var config: BilhedBackConfig

    @RelaxedMockK
    lateinit var smsSend: SendRegistrationSms

    @RelaxedMockK
    lateinit var sendNotification: SendNotification

    @RelaxedMockK
    lateinit var groupPort: GroupPort

    @RelaxedMockK
    lateinit var personPort: PersonPort

    @RelaxedMockK
    lateinit var registrationInfoPort: RegistrationInfoPort

    @RelaxedMockK
    lateinit var registration: Registration

    lateinit var referent: PersonRegister

    @BeforeEach
    fun setup() {

        referent = PersonRegister(
            lastname = "Dupont",
            firstname = "Jean",
            telephone = "+33612345678",
            email = "jean.dupont@example.com",
        )

        registration = Registration(
            config = config,
            smsSend = smsSend,
            sendNotification = sendNotification,
            groupPort = groupPort,
            personPort = personPort,
            registrationInfoPort = registrationInfoPort,
        )
    }

    @Test
    fun `single person registration`() {
        /**
         * Inscription d'une personne seule — groupe où la personne est référente
         */

        registration.register(
            ref = referent,
            groupPayment = true,
            companions = emptyList(),
            pass = PassType.TWO_DAYS
        )

        verify(exactly = 1) { personPort.save(any()) }
        verify(exactly = 1) { registrationInfoPort.save(any()) }
        verify(exactly = 1) { groupPort.save(any()) }
        verify(exactly = 1) { smsSend.sendSms(any(), any()) }
    }

    @Test
    fun `group registration with grouped payment`() {
        /**
         * Inscription d'un groupe avec paiement groupé
         * Le/les accompagnant(s) n'ont pas de numéro de téléphone
         */

        val companion = PersonRegister(
            lastname = "Ompaniant",
            firstname = "Hack",
            email = "hack.ompaniant@mail.fr",
            telephone = null
        )

        registration.register(
            ref = referent,
            groupPayment = true,
            companions = listOf(companion),
            pass = PassType.TWO_DAYS
        )

        verify(exactly = 2) { personPort.save(any()) }
        verify(exactly = 1) { registrationInfoPort.save(any()) }
        verify(exactly = 1) { groupPort.save(any()) }
        verify(exactly = 1) { smsSend.sendSms(any(), any()) }

    }

    @Test
    fun `group registration with separated payment`() {
        /**
         * Inscription d'un groupe avec paiement séparé
         * Le/les accompagnant(s) ont un numéro de téléphone
         */

        val companion = PersonRegister(
            lastname = "Ompaniant",
            firstname = "Hack",
            email = "hack.ompaniant@mail.fr",
            telephone = "+33622255885"
        )

        registration.register(
            ref = referent,
            groupPayment = true,
            companions = listOf(companion),
            pass = PassType.TWO_DAYS
        )

        verify(exactly = 2) { personPort.save(any()) }
        verify(exactly = 1) { registrationInfoPort.save(any()) }
        verify(exactly = 1) { groupPort.save(any()) }
        verify(exactly = 1) { smsSend.sendSms(any(), any()) }

    }
}