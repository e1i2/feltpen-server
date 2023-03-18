package io.github.e1i2.global.security.authentication

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class AuthenticationService {
    suspend fun currentUserIdOrThrow(): Long {
        return currentUserId() ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
    }

    suspend fun currentUserId(): Long? {
        return ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()
            ?.authentication?.principal?.toString()?.toLongOrNull()
    }
}