package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.ParticipationInfo
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.*

interface ParticipationInfoPort {
    fun get(id: UUID): ParticipationInfo

    fun get(ids: List<UUID>): List<ParticipationInfo>

    fun save(partInfos: ParticipationInfo)

    fun list(status: PersonStatus): List<ParticipationInfo>

    fun getByGroup(id: UUID): List<ParticipationInfo>

    fun getByGroups(ids: List<UUID>): List<ParticipationInfo>

    fun existsByPersonId(id: UUID): Boolean

    fun updateConfirmationDate(id: UUID, confirmationDate: ZonedDateTime)

    fun updateSms(id: UUID, smsStatus: SmsStatus, error: String?)

    fun updateNotification(id: UUID, notificationDate: ZonedDateTime)

    fun setPayed(ids: List<UUID>)
}