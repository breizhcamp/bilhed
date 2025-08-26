package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.entities.RegistrationInfo
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.UUID

interface RegistrationInfoPort {

    fun list(status: PersonStatus): List<RegistrationInfo>

    fun save(infos: RegistrationInfo)

    fun get(id: UUID): RegistrationInfo

    fun get(ids: List<UUID>): List<RegistrationInfo>

    fun resetSmsCount(id: UUID)

    fun updateSms(id: UUID, smsStatus: SmsStatus, error: String? = null,
                  nbSmsSent: Int? = null, lastSmsSentDate: ZonedDateTime? = null)
}