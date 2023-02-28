package io.github.e1i2.global.security.authentication

import io.github.e1i2.global.security.JwtProperties
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationConverter(
    private val jwtProperties: JwtProperties
) : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> = mono {
        val authorization = exchange.request.cookies["access-token"]?.firstOrNull()

        authorization?.let {
            val tokenAuthentication = TokenAuthentication(jwtProperties.secretKey, it.value)
            val claims = tokenAuthentication.parseTokenOrThrowException()

            UsernamePasswordAuthenticationToken(claims.body.subject, "")
        }
    }
}