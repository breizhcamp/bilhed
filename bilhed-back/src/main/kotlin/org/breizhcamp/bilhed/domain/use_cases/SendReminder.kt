package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.*
import org.breizhcamp.bilhed.infrastructure.TimeService
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*


@Service
class SendReminder (
    private val reminderPort: ReminderPort,
    private val reminderConfigPort: ReminderConfigPort,
    private val registeredReminder: RegisteredReminder,
    private val participantNotif: ParticipantNotif,
    private val attendeeNotify: AttendeeNotify,
    private val configPort: ConfigPort,
    private val timeService: TimeService,
    private val referentInfosPort: ReferentInfosPort,
    private val participationInfosPort: ParticipationInfosPort,
    private val personPort: PersonPort,
) {

    private fun areConditionsMet(deadline: ZonedDateTime, reminderConfigs: List<ReminderConfig>, now: ZonedDateTime, notif: Reminder?): ReminderConfig? {
        val (prevRemConfig, nextRemDate) = reminderConfigs
            .map { it to deadline.minusHours(it.hours.toLong()) } // map en config -> date
            .partition { (_, configDate) -> configDate.isBefore(now) } // separe en deux listes : passées et futures
            .let { (past, future) ->
                val prev = past.maxByOrNull { it.second } // prev plus proche de now
                val next = future.minByOrNull { it.second } // next plus proche de now
                prev?.first to next?.second
            }

        // si prochain rappel && heure prochain rappel < now + 55min
        if (nextRemDate?.isBefore(now.plusMinutes(55)) != false) return null

        // si précédente notif envoyée == null (n'arrive pas, il aura normalement toujours une notification dans son historique).
        val prevNotif = notif ?: return null

        if (prevRemConfig == null) return null
        // si date précédente config <= date précédente notif envoyé < now
        val prevRemConfigDate = deadline.minusHours(prevRemConfig.hours.toLong())
        if (prevNotif.reminderDate.isBefore(now) && (
                prevRemConfigDate.isBefore(prevNotif.reminderDate) || prevRemConfigDate.isEqual(prevNotif.reminderDate)
            )) return null

        return prevRemConfig
    }

    private fun getNotifications(ids: List<UUID>):  Map<UUID, Reminder> {
        val notificationsList = reminderPort.findLatestReminderPerPerson(ids)
        return notificationsList.associateBy { it.personId }
    }

    fun sendRegisteredReminder() {
        val now = timeService.now()
        val maxTime = configPort.get("reminderTimeReg").value.toLong()

        val reminderConfigs = reminderConfigPort.listByType("REGISTERED")
        var registers = referentInfosPort.list()
        // On filtre ceux dont la deadline est dépassée (car ils ne sont pas RELEASED).
        registers = registers.filter { it.registrationDate.plusHours(maxTime).isAfter(now) }

        val notifications = getNotifications(registers.map { it.personId })

        for (reg in registers) {
            val deadline = reg.registrationDate.plusHours(maxTime)
            val prevRemConfig = areConditionsMet(deadline, reminderConfigs, now, notifications[reg.personId]) ?: continue

            registeredReminder.send(
                reg.personId,
                prevRemConfig.templateSms,
                prevRemConfig.templateMail,
                ReminderOrigin.AUTOMATIC
            )
        }
    }

    fun sendParticipantReminder() {
        val now = timeService.now()
        val maxTime = configPort.get("reminderTimePar").value.toLong()

        val reminderConfigs = reminderConfigPort.listByType("PARTICIPANT")
        val participants = participationInfosPort.list()
        val notifications = getNotifications(participants.map { it.personId })

        for (par in participants) {
            val deadline = par.notificationConfirmSentDate?.plusHours(maxTime)
            if (deadline == null) continue // tirage au sort pas encore effectué
            val prevRemConfig = areConditionsMet(deadline, reminderConfigs, now, notifications[par.personId]) ?: break

            participantNotif.remindSuccess(listOf(par.personId), ReminderOrigin.AUTOMATIC, prevRemConfig.templateMail)
        }
    }

    fun sendAttendeeReminder() {
        val now = timeService.now()
        val maxTime = configPort.get("reminderTimeAtt").value.toLong()

        val reminderConfigs = reminderConfigPort.listByType("ATTENDEE")
        val attendees = personPort.filter(PersonFilter(status = PersonStatus.ATTENDEE, payed = false))
        val partInfos = participationInfosPort.get(attendees.map { it.id })

        val notifications = getNotifications(attendees.map { it.id })

        for (att in attendees) {
            val infos = partInfos.firstOrNull { it.personId == att.id } ?: continue
            if (infos.confirmationDate == null) continue

            val deadline = infos.confirmationDate.plusHours(maxTime)
            val prevRemConfig = areConditionsMet(deadline, reminderConfigs, now, notifications[att.id]) ?: continue

            if (prevRemConfig.templateMail.isNotBlank())
                attendeeNotify.remindPayedMail(listOf(att.id), ReminderOrigin.AUTOMATIC, prevRemConfig.templateMail)

            if (prevRemConfig.templateSms.isNotBlank())
                attendeeNotify.remindPayedSms(listOf(att.id), ReminderOrigin.AUTOMATIC, prevRemConfig.templateSms)
        }
    }
}