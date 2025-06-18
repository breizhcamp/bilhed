package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class ParticipantConfirm(
    private val ticketPort: TicketPort,
    private val personPort: PersonPort,
    private val configPort: ConfigPort,
    private val groupPort: GroupPort,
    private val partInfosPort: ParticipationInfosPort,
    private val attendeeDataPort: AttendeeDataPort,
    private val personRelease: PersonRelease
) {

    fun get(id: UUID): ParticipantConfirmInfo {
        val pers = personPort.getParticipant(id)
        val group = groupPort.get(pers.groupId)
        val ref = personPort.getReferentOfGroup(group.id)

        if (group.drawOrder == null)
            throw IllegalArgumentException("Vous n'avez pas été tiré au sort")

        if (group.groupPayment && ref.id != pers.id) // paiement groupé, pas ref
            throw IllegalArgumentException("Ce participant ne doit pas confirmer.")

        val partInfos = partInfosPort.get(pers.id)
        newCheckLimiteDate(partInfos)

        val companions = mutableListOf<Person>() // paiement séparé, ref

        if (group.groupPayment) // paiement groupé, ref
            companions.addAll(personPort.getCompanions(group.id, group.referentId))

        if (ref.id != pers.id) // paiement séparé, pas ref
            companions.add(pers)

        return ParticipantConfirmInfo(
            group.groupPayment,
            ref,
            companions,
            partInfos.notificationConfirmSentDate!!,
        )
    }

    private fun newCheckLimiteDate(partInfos: ParticipationInfos) {
        val limitDate = requireNotNull(partInfos.notificationConfirmSentDate) { "La notification de succès n'a pas été envoyée" }
        val limitTime = configPort.get("reminderTimePar")

        if (limitDate.plusHours(limitTime.value.toLong()).isBefore(ZonedDateTime.now())) {
            throw IllegalArgumentException("Vous avez dépassé la date limite de confirmation, votre place a été remise en jeu")
        }
    }

    @Transactional
    fun confirm(attendees: List<Pair<UUID, AttendeeData>>): Ticket {
        // TODO : /!\ Créer une commande
        for (att in attendees) {
            val p = personPort.get(att.first)

            logger.info { "Save attendee data for participant [${att.first}] / [${p.lastname}] [${p.firstname}]" }
            attendeeDataPort.saveData(att.first, att.second)
        }


//        return when (p) {
//            is Participant -> {
//                logger.info { "Level up participant to attendee [$id]" }
//                participantPort.levelUpToAttendee(id)
//
//                logger.info { "Create ticket for participant [$id] / [${p.lastname}] [${p.firstname}]" }
//                val ticket = ticketPort.create(p)
//                logger.info { "Ticket created for participant [$id] / [${p.lastname}] [${p.firstname}]" }
//
//                ticket
//            }
//
//            is Attendee -> Ticket(ticketPort.getPayUrl(id), p.payed)
//
//            else -> throw IllegalStateException("Participant [$id] / [${p.lastname}] [${p.firstname}] is not a participant or attendee")
//        }
        return Ticket("", false)
    }

    @Transactional
    fun confirmList(ids: List<UUID>): List<Ticket> {
        val participants = ids.map {
            logger.info { "Level up participant to attendee [$it]" }
            personPort.levelUpTo(it, PersonStatus.ATTENDEE).also { p ->
                logger.info { "Participant [$it] / [${p.lastname}] [${p.firstname}] leveled up to attendee" }
            }
        }

        logger.info { "Create tickets for [${participants.size}] participants" }
        // TODO : Tickets
        val tickets = ticketPort.create(participants)
        logger.info { "[${tickets.size}] tickets created for [${participants.size}] participants" }
        return tickets
    }



    @Transactional
    fun cancel(id: UUID) {
        val p = personPort.get(id)
        logger.info { "Level up participant to release [$id] / [${p.lastname}] [${p.firstname}]" }
        personRelease.release(id)
    }

    @Transactional
    fun release(ids: List<UUID>) = ids.forEach {
        logger.info { "Level up participant to release [$it]" }
        personRelease.release(it)
    }
}