package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.ReferentInfos
import org.breizhcamp.bilhed.domain.use_cases.ports.ReferentInfosPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReferentInfosCrud(
    val referentInfosPort: ReferentInfosPort
) {
    fun get(id: UUID): ReferentInfos = referentInfosPort.get(id)

    fun get(ids: List<UUID>): List<ReferentInfos> = referentInfosPort.get(ids)
}