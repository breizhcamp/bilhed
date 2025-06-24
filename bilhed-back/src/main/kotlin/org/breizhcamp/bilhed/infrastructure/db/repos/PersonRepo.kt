package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDBStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PersonRepo: JpaRepository<PersonDB, UUID>, PersonRepoCustom {

    fun findPersonById(id: UUID): PersonDB?

    @Query("select p from PersonDB p where p.status = 'ATTENDEE'")
    fun listAttendees(): List<PersonDB>

    @Modifying
    @Query("UPDATE PersonDB p SET p.payed = true WHERE p.id IN (:ids)")
    fun setPayed(ids: List<UUID>)

    fun countByEmailOrTelephone(email: String, telephone: String?): Int
    fun countByEmail(email: String): Int

    @Query("select p.pass, count(p) from PersonDB p where p.id in ( " +
            "    select pi.person " +
            "    from ParticipationInfosDB pi " +
            "    where pi.participantConfirmationDate is not null ) " +
            "group by p.pass")
    fun countAlreadyNotif(): List<Pair<PassType, Int>>

    @Modifying
    @Query("UPDATE PersonDB p SET p.email = :email WHERE p.id = :id")
    fun updateEmail(id: UUID, email: String)

    @Query("SELECT p FROM PersonDB p JOIN FETCH p.group WHERE p.group.id = :groupId AND p.id <> :referentId")
    fun getCompanions(groupId: UUID, referentId: UUID): List<PersonDB>

    fun findByGroupId(id: UUID): List<PersonDB>

    fun findByGroupIdAndStatus(groupId: UUID, status: PersonDBStatus): List<PersonDB>

    @Query("SELECT p FROM PersonDB p JOIN FETCH p.group WHERE p.id = p.group.referentId AND p.group.id = :groupId")
    fun findReferentOfGroup(groupId: UUID): PersonDB
}