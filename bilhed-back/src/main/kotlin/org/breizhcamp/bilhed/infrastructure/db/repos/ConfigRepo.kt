package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.ConfigDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigRepo: JpaRepository<ConfigDB, String>