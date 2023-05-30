package org.breizhcamp.bilhed.infrastructure.db

import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.infrastructure.db.model.AttendeeDataDB
import org.breizhcamp.bilhed.infrastructure.db.repos.AttendeeDataRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class AttendeeAdapter(
    private val attendeeDataRepo: AttendeeDataRepo
): AttendeePort {

    override fun saveData(id: UUID, data: AttendeeData) {
        attendeeDataRepo.save(data.toDB(id))
    }

}

private fun AttendeeData.toDB(id: UUID) = AttendeeDataDB(
    id = id,
    company = company,
    tShirtSize = tShirtSize,
    tShirtCut = tShirtCut,
    vegan = vegan,
    meetAndGreet = meetAndGreet,
    postalCode = postalCode,
)