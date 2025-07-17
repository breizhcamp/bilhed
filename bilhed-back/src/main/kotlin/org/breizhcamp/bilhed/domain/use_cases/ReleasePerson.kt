package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.*
import org.breizhcamp.bilhed.infrastructure.TimeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class ReleasePerson(
    private val personPort: PersonPort,
    private val configPort: ConfigPort,
    private val ticketPort: TicketPort,
    private val timeService: TimeService,
    private val groupPort: GroupPort,
    private val partInfosPort: ParticipationInfosPort,
) {

    @Transactional
    fun manualAttendeeRelease(ids: List<UUID>) {
        val attendees = personPort.get(ids).groupBy { it.groupId }

        val groupMap = groupPort.getBy(memberIds = ids).associateBy { it.id }
        val partInfosList = partInfosPort.getByGroups(groupMap.keys.toList())

        val extendedGroups = groupMap.mapNotNull { (groupId, group) ->
            attendees[groupId]?.let { personList ->
                group to personList
            }
        }.toMap()

        attendeeRelease(groupMap = extendedGroups, partInfosList = partInfosList)
    }

    @Transactional
    fun attendeeRelease(groupMap: Map<Group, List<Person>>, partInfosList: List<ParticipationInfos>) {
        val timeReminderAtt = configPort.get("reminderTimeAtt").value.toLong()
        val now = timeService.now()

        val personsToDeleteTicket = mutableListOf<Person>()
        for ((group, members) in groupMap.entries) {
            if (group.groupPayment) {
                val partInfos = partInfosList.find { it.personId == group.referentId } ?: throw IllegalStateException("Participation Infos not found")
                if (shouldAttendeeBeReleased(partInfos, timeReminderAtt, now)) {
                    val ref = members.find { it.id == group.referentId } ?: throw IllegalStateException("Referent not found")
                    members.forEach { release(pers = it, hasPartInfos = ref.id == it.id) }
                    personsToDeleteTicket.add(ref)
                }
            } else {
                members.forEach { member ->
                    val partInfos = partInfosList.find { it.personId == member.id } ?: throw IllegalStateException("Participation Infos not found")
                    if (shouldAttendeeBeReleased(partInfos, timeReminderAtt, now)) {
                        release(pers = member, hasPartInfos = true)
                        personsToDeleteTicket.add(member)
                    }
                }
            }
        }
        if (personsToDeleteTicket.isNotEmpty()) {
            logger.info { "Release [${personsToDeleteTicket.size}] attendees: " + personsToDeleteTicket.joinToString { "${it.lastname} ${it.firstname}" } }
            ticketPort.delete(personsToDeleteTicket)
            logger.info { "[${personsToDeleteTicket.size}] Attendees released"}
        }
    }

    @Transactional
    fun participantReleaseAuto() {
        /**
         * Persons who could be released should:
         * - be PARTICIPANT
         * if referent:
         * - have participationInfos
         * - have notificationConfirmSentDate (notification after draw)
         */
        val timeReminderPar = configPort.get("reminderTimePar").value.toLong()
        val now = timeService.now()

        val partInfosList = partInfosPort.list(PersonStatus.PARTICIPANT)
        // get only referent or persons who have participationInfos
        val persons = personPort.get(partInfosList.map { it.personId }).associateBy { it.id }

        partInfosList.forEach {
            if (it.notificationConfirmSentDate == null) return@forEach // notif de succès du tirage pas envoyé

            val deadline = it.notificationConfirmSentDate.plusHours(timeReminderPar)
            if (deadline.isBefore(now)) {
                val person = persons[it.personId] ?: throw IllegalStateException("Person not found")
                val extendedGroup = groupPort.extendedGroupBy(groupId = person.groupId)
                if (!extendedGroup.first.groupPayment) { // not a referent, release the person
                    release(pers = person, hasPartInfos = true)
                    return@forEach
                }

                // release all the group, person variable is the referent
                extendedGroup.second.forEach { m -> release(m, m.id == person.id) }

                logger.info { "Participant [${it.personId}] released, limit was $deadline" }
            }
        }
    }

    @Transactional
    fun attendeeReleaseAuto() {
        /**
         * Persons who could be released should:
         * - be ATTENDEE
         * - have not paid yet
         * if referent:
         * - have participationInfos
         * - have participationDate (place confirmed after draw)
         */

        val groupMap = groupPort.extendedGroupList(PersonFilter(status = PersonStatus.ATTENDEE, payed = false))
        val partInfosList = partInfosPort.getByGroups(groupMap.keys.map { it.id })

        attendeeRelease(groupMap = groupMap, partInfosList = partInfosList)
    }

    private fun shouldAttendeeBeReleased(partInfos: ParticipationInfos, reminderTime: Long, now: ZonedDateTime): Boolean {
        if (partInfos.confirmationDate == null) return false
        val deadline = partInfos.confirmationDate.plusHours(reminderTime)
        return deadline.isBefore(now)
    }

    fun release(pers: Person, hasPartInfos: Boolean = false) {
        personPort.levelUpTo(pers.id, PersonStatus.RELEASED)

        if (hasPartInfos && (pers.status == PersonStatus.PARTICIPANT || pers.status == PersonStatus.ATTENDEE)) {
            partInfosPort.updateConfirmationDate(pers.id, ZonedDateTime.now(ZoneId.of("Europe/Paris")))
        }
    }
}