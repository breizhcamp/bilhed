package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ParticipantRepo: JpaRepository<ParticipantDB, UUID>, ParticipantRepoCustom {

    @Query("select p from ParticipantDB p where p.id = :id and p.status = 'PARTICIPANT'")
    fun findParticipant(id: UUID): ParticipantDB?

    @Query("select p from ParticipantDB p where p.status = 'REGISTERED' order by p.registrationDate asc")
    fun listRegistered(): List<ParticipantDB>

    fun countByEmailOrTelephone(email: String, telephone: String): Int
    fun countByTelephone(phone: String): Int

    @Query("select p from ParticipantDB p where p.status = 'PARTICIPANT' AND p.drawOrder is null")
    fun listParticipantWithNoDraw(): List<ParticipantDB>

    @Query("select p from ParticipantDB p where p.status = 'PARTICIPANT' AND p.drawOrder is not null AND p.pass = :pass order by p.drawOrder asc")
    fun listByPass(pass: PassType, page: Pageable): List<ParticipantDB>

    @Query("select new kotlin.Pair(p.pass, COUNT(p)) from ParticipantDB p WHERE p.status = 'PARTICIPANT' AND p.participantConfirmationDate is not null GROUP BY p.pass")
    fun countAlreadyNotif(): List<Pair<PassType, Int>>

    @Modifying
    @Query("UPDATE ParticipantDB p SET p.drawOrder = :drawOrder WHERE p.id = :id")
    fun updateDrawOrder(id: UUID, drawOrder: Int)

    @Modifying
    @Query("UPDATE ParticipantDB p SET p.registrationNbSmsSent = 0 WHERE p.id = :id")
    fun resetSmsCount(id: UUID)

    @Modifying
    @Query("UPDATE ParticipantDB p SET p.payed = true WHERE p.id IN (:ids)")
    fun setPayed(ids: List<UUID>)
}