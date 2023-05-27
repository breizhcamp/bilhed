package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ParticipantRepo: JpaRepository<ParticipantDB, UUID> {

    @Query("select p from ParticipantDB p where p.status = 'REGISTERED' order by p.registrationDate asc")
    fun listRegistered(): List<ParticipantDB>

    @Query("select p from ParticipantDB p where p.status = 'PARTICIPANT' order by p.participationDate asc")
    fun listParticipants(): List<ParticipantDB>

    fun countByEmailOrTelephone(email: String, telephone: String): Int
    fun countByTelephone(phone: String): Int

    @Query("select p from ParticipantDB p where p.status = 'PARTICIPANT' AND p.drawOrder is null")
    fun listParticipantWithNoDraw(): List<ParticipantDB>

    @Modifying
    @Query("UPDATE ParticipantDB p SET p.drawOrder = :drawOrder WHERE p.id = :id")
    fun updateDrawOrder(id: UUID, drawOrder: Int)

    @Modifying
    @Query("UPDATE ParticipantDB p SET p.registrationNbSmsSent = 0 WHERE p.id = :id")
    fun resetSmsCount(id: UUID)
}