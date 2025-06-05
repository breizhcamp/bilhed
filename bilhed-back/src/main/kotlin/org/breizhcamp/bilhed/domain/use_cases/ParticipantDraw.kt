package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class ParticipantDraw(
    private val personPort: PersonPort,
) {

    @Transactional
    fun draw() {
        personPort.listIdsWithNoDraw().forEach {
            val ids = it.value.shuffled()
            logger.info { "Drawing [${ids.size}] participants for [${it.key}]" }

            ids.forEachIndexed { index, id ->
                personPort.updateDrawOrder(id, index)
            }

            logger.info { "[${ids.size}] participants drawn" }
        }
    }
}