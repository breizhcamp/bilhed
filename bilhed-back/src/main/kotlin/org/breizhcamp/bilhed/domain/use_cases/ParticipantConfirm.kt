package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
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
    private val personRelease: PersonRelease,
) {

    fun getConfirmInfos(persId: UUID): ParticipantConfirmInfo {
        val group = groupPort.getByMemberId(persId)
        val members = personPort.getMembersByGroup(group.id)
        val pair = members.partition { it.id == group.referentId }

        if (group.drawOrder == null)
            throw IllegalArgumentException("Vous n'avez pas été tiré au sort")

        if (group.groupPayment && pair.first.single().id != persId) // paiement groupé, pas ref
            throw IllegalArgumentException("Ce participant ne doit pas confirmer.")

        val limitDate = getLimitDate(partInfosPort.get(persId))

        if (group.groupPayment) // paiement groupé, ref
            return ParticipantConfirmInfo(
                pair.first + pair.second,
                limitDate,
            )

        // paiement séparé
        return ParticipantConfirmInfo(
            listOf(members.find { it.id == persId }!!),
            limitDate,
        )

    }

    private fun getLimitDate(partInfos: ParticipationInfos): ZonedDateTime {
        val notifSentDate = requireNotNull(partInfos.notificationConfirmSentDate) { "La notification de succès n'a pas été envoyée" }
        val limitTime = configPort.get("reminderTimePar")

        val limitDate = notifSentDate.plusHours(limitTime.value.toLong())
        if (limitDate.isBefore(ZonedDateTime.now())) {
            throw IllegalArgumentException("Vous avez dépassé la date limite de confirmation, votre place a été remise en jeu")
        }
        return limitDate
    }

    @Transactional
    fun confirm(attendeesReq: List<Pair<UUID, AttendeeData>>): Ticket {
        /**
         * Confirmation from participant
         * Confirm one person or one group, depends on attendeesReq length
         */

        if (attendeesReq.size == 1) {
            // confirm one person
            val member = personPort.get(attendeesReq.first().first)
            val gr = groupPort.get(member.groupId)
            return confirmOne(listOf(member), gr, attendeesReq).first()
        }

        val members = personPort.get(attendeesReq.map { it.first })
        val group = groupPort.get(members.first().groupId)
        return confirmOne(members, group, attendeesReq).first() // ticket of referent
    }

    @Transactional
    fun confirmOne(members: List<Person>, group: Group, attendeesReq: List<Pair<UUID, AttendeeData>>): List<Ticket> {
        // confirm one group
        val ref = members.find { it.groupId == group.id } ?: throw IllegalStateException("Referent of group [${group.id}] not found")

        if (ref.status == PersonStatus.ATTENDEE) {
            return members.map { Ticket(ticketPort.getPayUrl(it.id), it.payed) }
        }

        // level up to attendee
        members.map {
            logger.info { "Level up participant to attendee [$it]" }
            personPort.levelUpTo(it.id, PersonStatus.ATTENDEE).also { p ->
                logger.info { "Participant [$it] / [${p.lastname}] [${p.firstname}] leveled up to attendee" }
            }
        }

        // save attendee data
        members.forEach {
            attendeeDataPort.saveData(it.id, attendeesReq.find { a -> a.first == it.id }!!.second)
        }

        // filter participants (persons who have participationInfos)
        val participants = members.filter { person -> !group.groupPayment || person.id == group.referentId }

        // change confirmation date
        participants.forEach {
            partInfosPort.updateConfirmationDate(it.id, ZonedDateTime.now(ZoneId.of("Europe/Paris")))
        }

        // be sure that referent is the first of members
        val membersSorted = members.sortedByDescending { it.id == group.referentId }

        logger.info { "Create tickets for [${membersSorted.size}] participants" }
        val tickets = ticketPort.create(membersSorted)
        logger.info { "[${tickets.size}] tickets created for [${membersSorted.size}] participants" }

        return tickets
    }

    @Transactional
    fun confirmList(ids: List<UUID>): List<Ticket> {
        /**
         * Manual confirmation, from admin
         * Confirm a list of groups or single persons
         */
        val persons = personPort.get(ids)
        val groups = groupPort.get(persons.map { it.groupId })

        val groupPerson = persons.groupBy { it.groupId }

        return groupPerson.map { (groupId, persons) ->
            val attendeesData = persons.map { it.id to AttendeeData(
                tShirtSize = "no", vegan = false, meetAndGreet = false,
                company = null, tShirtCut = null, postalCode = null
            ) }
            confirmOne(persons, groups.find { it.id == groupId }!!, attendeesData) }.flatten()
    }



    @Transactional
    fun cancel(id: UUID) {
        /**
         * Cancel the entire group if id is the referent id
         * Otherwise, cancel the participant
         */
        val p = personPort.get(id)
        val group = groupPort.getByMemberId(id)

        if (group.groupPayment && id == group.referentId) {
            val comp = personPort.getCompanions(group.id, id)
            comp.forEach {
                logger.info { "Level up participant to release [${it.id}] / [${it.lastname}] [${it.firstname}]" }
                personRelease.release(it)
            }
            logger.info { "Level up participant to release [$id] / [${p.lastname}] [${p.firstname}]" }
            personRelease.release(p, true)
            return
        }

        logger.info { "Level up participant to release [$id] / [${p.lastname}] [${p.firstname}]" }
        personRelease.release(p, id == group.referentId)
    }
}