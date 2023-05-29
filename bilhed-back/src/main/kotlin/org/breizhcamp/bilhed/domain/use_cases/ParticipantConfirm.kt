package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*

@Service
class ParticipantConfirm(
    private val participantPort: ParticipantPort,
) {

    fun get(id: UUID): Participant {
        val p = participantPort.get(id)
        val limitDate = requireNotNull(p.confirmationLimitDate) { "Vous n'avez pas été tiré au sort" }
        if (limitDate.isBefore(ZonedDateTime.now())) {
            throw IllegalStateException("Vous avez dépassé la date limite de confirmation, votre place a été remise en jeu")
        }
        return p
    }

}