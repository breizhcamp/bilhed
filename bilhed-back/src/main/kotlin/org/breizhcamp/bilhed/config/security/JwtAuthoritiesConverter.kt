package org.breizhcamp.bilhed.config.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class JwtAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val access = jwt.claims["realm_access"] as? Map<*, *>
        val roles = access?.get("roles") as? List<*>

        return roles
            ?.filterIsInstance(String::class.java)
            ?.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }
            ?: emptyList()
    }
}