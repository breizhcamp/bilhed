package org.breizhcamp.bilhed.infrastructure.db.repos

import com.querydsl.jpa.JPQLQuery
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB
import org.breizhcamp.bilhed.infrastructure.db.model.QGroupDB
import org.breizhcamp.bilhed.infrastructure.db.model.QPersonDB
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class PersonRepoImpl: QuerydslRepositorySupport(PersonDB::class.java), PersonRepoCustom {

    override fun filterPerson(filter: PersonFilter): List<PersonDB> {
        val p = QPersonDB.personDB
        val g = QGroupDB.groupDB
        val query = from(p).leftJoin(p.group, g).fetchJoin().where(p.status.eq(filter.status?.toDB()))

        filterPersonInfos(filter, query, p)

        return query.fetch()
    }

    private fun filterPersonInfos(filter: PersonFilter, query: JPQLQuery<PersonDB>, p: QPersonDB) {
        filter.lastname?.let { query.where(p.lastname.likeIgnoreCase("%$it%")) }
        filter.firstname?.let { query.where(p.firstname.likeIgnoreCase("%$it%")) }
        filter.email?.let { query.where(p.email.likeIgnoreCase("%$it%")) }
        filter.pass?.let { query.where(p.pass.eq(it)) }
        filter.payed?.let { query.where(p.payed.eq(it)) }
        filter.groupId?.let { query.where(p.group.id.eq(it)) }
    }

}