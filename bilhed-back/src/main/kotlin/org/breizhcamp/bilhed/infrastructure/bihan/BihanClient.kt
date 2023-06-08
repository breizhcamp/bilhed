package org.breizhcamp.bilhed.infrastructure.bihan

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.PostExchange

interface BihanClient {

    @PostExchange("/api/links")
    fun createLink(@RequestBody req: AddLinkReq): AddLinkRes

}