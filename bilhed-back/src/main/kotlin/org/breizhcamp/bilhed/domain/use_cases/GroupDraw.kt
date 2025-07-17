package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.use_cases.ports.GroupPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class GroupDraw(
    private val groupPort: GroupPort,
) {

    @Transactional
    fun draw() {
        groupPort.listIdsWithNoDraw().forEach {
            val ids = it.value.shuffled()
            logger.info { "Drawing [${ids.size}] groups for [${it.key}]" }

            ids.forEachIndexed { index, id ->
                groupPort.updateDrawOrder(id, index)
            }

            logger.info { "[${ids.size}] groups drawn" }
        }
    }
}