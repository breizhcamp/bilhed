package org.breizhcamp.bilhed.infrastructure.bihan

import java.time.ZonedDateTime

data class AddLinkReq(
    val url: String,
    val expirationDate: ZonedDateTime?
)
