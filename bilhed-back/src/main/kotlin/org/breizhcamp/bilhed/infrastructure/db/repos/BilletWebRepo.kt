package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.BilletWebDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BilletWebRepo: JpaRepository<BilletWebDB, UUID>