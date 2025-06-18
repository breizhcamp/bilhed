package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.*
import org.breizhcamp.bilhed.infrastructure.TimeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class PersonRelease(
    private val personPort: PersonPort,
    private val configPort: ConfigPort,
    private val ticketPort: TicketPort,
    private val timeService: TimeService,
    private val groupPort: GroupPort,
    private val partInfosPort: ParticipationInfosPort
) {

    @Transactional
    fun attendeeRelease(ids: List<UUID>) {
        val attendees = personPort.get(ids)
        logger.info { "Release [${attendees.size}] attendees: " + attendees.joinToString { "${it.lastname} ${it.firstname}" } }
        ticketPort.delete(attendees)
        attendees.forEach { release(it.id) }
        logger.info { "[${attendees.size}] Attendees released"}
    }

    @Transactional
    fun participantReleaseAuto() {
        val timeReminderPar = configPort.get("reminderTimePar").value.toLong()
        val now = timeService.now()

        partInfosPort.list().forEach {
            if (it.notificationConfirmSentDate == null) return@forEach // notif de succès du tirage pas envoyé

            val deadline = it.notificationConfirmSentDate.plusHours(timeReminderPar)
            if (deadline.isBefore(now)) {
                release(it.personId)
                logger.info { "Participant [${it.personId}] released, limit was $deadline" }
            }
        }
    }

    @Transactional
    fun attendeeReleaseAuto() {
        val timeReminderAtt = configPort.get("reminderTimeAtt").value.toLong()
        val now = timeService.now()

        // TODO : faire en sorte d'avoir que les gens qui doivent payer (donc paiement groupé et ref ou paiement séparé)
        val ids = mutableListOf<UUID>()
        groupPort.list().forEach {
            membersMustPay(it).forEach { payer ->
                val partInfos = partInfosPort.get(payer.id)
                val deadline = partInfos.confirmationDate!!.plusHours(timeReminderAtt)
                if (!payer.payed && deadline.isBefore(now))
                    ids.add(payer.id)
            }
        }
        if (ids.isNotEmpty()) attendeeRelease(ids)

//          OLD fun
//        val ids: List<UUID> = personPort.filter(PersonFilter(status = PersonStatus.ATTENDEE)).mapNotNull {
//
//            val deadline = it.participantConfirmationDate.plusHours(timeReminderAtt)
//            if (!it.payed && deadline.isBefore(now)) it.id else null
//        }
//
//        if (ids.isNotEmpty()) attendeeRelease(ids)
    }

    private fun membersMustPay(group: Group): List<Person> {
        // TODO : /!\ Diff entre personne attendee et personne avec date de confirmation ?
        // A voir pour opti
        if (group.groupPayment && group.drawOrder != null) {
            val ref = personPort.getReferentOfGroup(group.id)
            return if(ref.status == PersonStatus.ATTENDEE) listOf(ref) else emptyList()
        }

        return personPort.getCompanions(group.id, group.referentId).filter { it.status == PersonStatus.ATTENDEE }
    }

    fun release(id: UUID) {
        personPort.levelUpTo(id, PersonStatus.RELEASED)
    }
}