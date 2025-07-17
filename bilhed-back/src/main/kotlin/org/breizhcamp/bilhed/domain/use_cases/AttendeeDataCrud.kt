package org.breizhcamp.bilhed.domain.use_cases

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeeDataPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AttendeeDataCrud(
    private val attendeeDataPort: AttendeeDataPort
) {
    fun get(id: UUID): AttendeeData {
        return attendeeDataPort.getData(id) ?: throw EntityNotFoundException("Données non trouvées pour [$id].")
    }
}