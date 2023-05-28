package org.breizhcamp.bilhed.domain.use_cases.ports

import java.time.ZonedDateTime

interface UrlShortenerPort {

    /**
     * Shorten a URL
     * @return the shortened URL
     */
    fun shorten(url: String, expirationDate: ZonedDateTime? = null): String

}