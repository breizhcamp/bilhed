package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.RegistrationInfos
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.UUID

interface RegistrationInfosPort {

    fun list(status: PersonStatus): List<RegistrationInfos>

    fun save(infos: RegistrationInfos)

    fun get(id: UUID): RegistrationInfos

    fun get(ids: List<UUID>): List<RegistrationInfos>

    fun resetSmsCount(id: UUID)

    fun updateSms(id: UUID, smsStatus: SmsStatus, error: String? = null,
                  nbSmsSent: Int? = null, lastSmsSentDate: ZonedDateTime? = null)
}