package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.ParticipationInfosDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ParticipationInfosRepo: JpaRepository<ParticipationInfosDB, UUID> {

}