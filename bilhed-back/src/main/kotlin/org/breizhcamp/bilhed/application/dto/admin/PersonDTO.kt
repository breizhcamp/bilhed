package org.breizhcamp.bilhed.application.dto.admin

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.breizhcamp.bilhed.domain.entities.PassType
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_status")
@JsonSubTypes(
    JsonSubTypes.Type(value = RegisteredDTO::class, name = "REGISTERED"),
    JsonSubTypes.Type(value = ParticipantDTO::class, name = "PARTICIPANT"),
    JsonSubTypes.Type(value = AttendeeDTO::class, name = "ATTENDEE"),
    JsonSubTypes.Type(value = AttendeeDataDTO::class, name = "ATTENDEE_FULL"),
)
abstract class PersonDTO {
    abstract val id: UUID

    abstract val lastname: String
    abstract val firstname: String
    abstract val email: String
    abstract val telephone: String
    abstract val pass: PassType
    abstract val kids: String?
}