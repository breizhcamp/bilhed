package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.domain.entities.PassType
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDBStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PersonRepo: JpaRepository<PersonDB, UUID>, PersonRepoCustom {

    @Query("select p from PersonDB p where p.id = :id and p.status = org.breizhcamp.bilhed.infrastructure.db.model.PersonDBStatus.PARTICIPANT")
    fun findParticipant(id: UUID): PersonDB?

    @Query("select p from PersonDB p where p.id = :id and p.status = 'ATTENDEE'")
    fun findAttendee(id: UUID): PersonDB?

    @Query("select p from PersonDB p where p.id IN (:ids) and p.status = 'ATTENDEE'")
    fun findAttendees(ids: List<UUID>): List<PersonDB>

    @Query("select p from PersonDB p where p.status = 'REGISTERED'")
    fun listRegistered(): List<PersonDB>

    @Query("select p from PersonDB p where p.status = 'ATTENDEE'")
    fun listAttendees(): List<PersonDB>

    @Modifying
    @Query("UPDATE PersonDB p SET p.payed = true WHERE p.id IN (:ids)")
    fun setPayed(ids: List<UUID>)

//    @Query("select p from PersonDB p where p.status = 'PARTICIPANT' AND p.drawOrder is not null AND p.pass = :pass order by p.drawOrder asc")
    @Query("select p from PersonDB p where p.status = 'PARTICIPANT' ")
    fun listByPass(pass: PassType, page: Pageable): List<PersonDB>

    fun countByEmailOrTelephone(email: String, telephone: String?): Int
    fun countByEmail(email: String): Int

//    @Query("select new kotlin.Pair(p.pass, COUNT(p)) from PersonDB p WHERE org.breizhcamp.bilhed.infrastructure.db.model.PersonDBStatus.PARTICIPANT  " +
//            "AND p.participantConfirmationDate is not null GROUP BY p.pass")
    @Query("select p.pass, count(p) from PersonDB p where p.id in ( " +
            "    select pi.personId " +
            "    from ParticipationInfosDB pi " +
            "    where pi.participantConfirmationDate is not null ) " +
            "group by p.pass")
    fun countAlreadyNotif(): List<Pair<PassType, Int>>

    @Modifying
    @Query("UPDATE PersonDB p SET p.status = org.breizhcamp.bilhed.infrastructure.db.model.PersonDBStatus.PARTICIPANT WHERE p.id = :id")
    fun levelUpToParticipant(id: UUID)

    @Modifying
    @Query("UPDATE PersonDB p SET p.email = :email WHERE p.id = :id")
    fun updateEmail(id: UUID, email: String)

    @Query("SELECT p FROM PersonDB p WHERE p.groupId = :groupId AND p.id <> :referentId")
    fun getCompanions(groupId: UUID, referentId: UUID): List<PersonDB>

    fun findByGroupId(id: UUID): List<PersonDB>

    @Query("SELECT p FROM PersonDB p WHERE p.id = ( SELECT g.referentId FROM GroupDB g WHERE g.id = :groupId )")
    fun findReferentOfGroup(groupId: UUID): PersonDB

    @Query("SELECT p FROM PersonDB p WHERE p.status = :status AND p.id in ( SELECT g.referentId FROM GroupDB g)")
    fun listReferents(status: PersonDBStatus): List<PersonDB>
}