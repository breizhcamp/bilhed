package org.breizhcamp.bilhed.infrastructure.db.repos

import com.querydsl.jpa.JPQLQuery
import org.breizhcamp.bilhed.domain.entities.AttendeeFilter
import org.breizhcamp.bilhed.domain.entities.ParticipantFilter
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDBStatus
import org.breizhcamp.bilhed.infrastructure.db.model.QParticipantDB
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class ParticipantRepoImpl: QuerydslRepositorySupport(ParticipantDB::class.java), ParticipantRepoCustom {

    override fun filterParticipant(filter: ParticipantFilter): List<ParticipantDB> {
        val p = QParticipantDB.participantDB
        val query = from(p).where(p.status.eq(ParticipantDBStatus.PARTICIPANT))

        filterPerson(filter, query, p)
        filter.success?.let {
            if (it) query.where(p.participantConfirmationLimitDate.isNotNull)
            else query.where(p.participantConfirmationLimitDate.isNull)
        }

        query.orderBy(p.pass.asc(), p.drawOrder.asc(), p.participationDate.asc())

        return query.fetch()
    }

    override fun filterAttendee(filter: AttendeeFilter): List<ParticipantDB> {
        val p = QParticipantDB.participantDB
        val query = from(p).where(p.status.eq(ParticipantDBStatus.ATTENDEE))

        filterPerson(filter, query, p)
        filter.payed?.let { query.where(p.payed.eq(it)) }

        query.orderBy(p.pass.asc(), p.participantConfirmationDate.asc())
        return query.fetch()
    }

    private fun filterPerson(filter: PersonFilter, query: JPQLQuery<ParticipantDB>, p: QParticipantDB) {
        filter.lastname?.let { query.where(p.lastname.likeIgnoreCase("%$it%")) }
        filter.firstname?.let { query.where(p.firstname.likeIgnoreCase("%$it%")) }
        filter.email?.let { query.where(p.email.likeIgnoreCase("%$it%")) }
        filter.pass?.let { query.where(p.pass.eq(it)) }
    }


}