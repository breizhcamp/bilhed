package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.domain.entities.ParticipantFilter
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDBStatus
import org.breizhcamp.bilhed.infrastructure.db.model.QParticipantDB
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class ParticipantRepoImpl: QuerydslRepositorySupport(ParticipantDB::class.java), ParticipantRepoCustom {

    override fun filter(filter: ParticipantFilter): List<ParticipantDB> {
        val p = QParticipantDB.participantDB

        val query = from(p).where(p.status.eq(ParticipantDBStatus.PARTICIPANT))

        filter.lastname?.let { query.where(p.lastname.likeIgnoreCase("%$it%")) }
        filter.firstname?.let { query.where(p.firstname.likeIgnoreCase("%$it%")) }
        filter.email?.let { query.where(p.email.likeIgnoreCase("%$it%")) }
        filter.pass?.let { query.where(p.pass.eq(it)) }
        filter.success?.let {
            if (it) query.where(p.participantConfirmationLimitDate.isNotNull)
            else query.where(p.participantConfirmationLimitDate.isNull)
        }

        query.orderBy(p.pass.asc(), p.drawOrder.asc(), p.participationDate.asc())

        return query.fetch()
    }
}