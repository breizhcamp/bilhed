package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ParticipantRepo: JpaRepository<ParticipantDB, UUID> {

    fun countByEmailOrTelephone(email: String, telephone: String): Int
    fun countByTelephone(phone: String): Int

}