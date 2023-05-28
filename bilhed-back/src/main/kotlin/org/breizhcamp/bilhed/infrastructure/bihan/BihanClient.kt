package org.breizhcamp.bilhed.infrastructure.bihan

import org.breizhcamp.bihan.application.dto.AddLinkReq
import org.breizhcamp.bihan.application.dto.AddLinkRes
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.PostExchange

interface BihanClient {

    @PostExchange("/api/add")
    fun createLink(@RequestBody req: AddLinkReq): AddLinkRes

}