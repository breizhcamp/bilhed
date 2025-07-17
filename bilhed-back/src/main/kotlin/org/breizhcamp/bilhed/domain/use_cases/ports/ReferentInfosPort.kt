package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.ReferentInfos
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.util.UUID

interface ReferentInfosPort {

    fun list(): List<ReferentInfos>

    fun save(infos: ReferentInfos)

    fun get(id: UUID): ReferentInfos

    fun get(ids: List<UUID>): List<ReferentInfos>

    fun resetSmsCount(id: UUID)

    fun updateSms(id: UUID, smsStatus: SmsStatus, error: String?)
}