package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.springframework.stereotype.Service

@Service
class RegisteredList(
    private val registeredPort: RegisteredPort,
) {

    fun list(): List<Registered> = registeredPort.list()

}