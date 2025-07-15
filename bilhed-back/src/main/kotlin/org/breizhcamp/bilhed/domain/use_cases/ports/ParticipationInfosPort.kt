package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.ParticipationInfos
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.*

interface ParticipationInfosPort {
    fun get(id: UUID): ParticipationInfos

    fun get(ids: List<UUID>): List<ParticipationInfos>

    fun save(partInfos: ParticipationInfos)

    fun list(status: PersonStatus): List<ParticipationInfos>

    fun getByGroup(id: UUID): List<ParticipationInfos>

    fun getByGroups(ids: List<UUID>): List<ParticipationInfos>

    fun existsByPersonId(id: UUID): Boolean

    fun updateConfirmationDate(id: UUID, confirmationDate: ZonedDateTime)

    fun updateSms(id: UUID, smsStatus: SmsStatus, error: String?)

    fun updateNotification(id: UUID, notificationDate: ZonedDateTime)
}