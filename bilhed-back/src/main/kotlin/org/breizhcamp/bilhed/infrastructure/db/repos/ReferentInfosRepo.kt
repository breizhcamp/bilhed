package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.ReferentInfosDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ReferentInfosRepo: JpaRepository<ReferentInfosDB, UUID> {

    @Modifying
    @Query("UPDATE ReferentInfosDB r SET r.registrationNbSmsSent = 0 WHERE r.personId = :id")
    fun resetSmsCount(id: UUID)
}