package io.github.e1i2.global.security.authentication

import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Configuration
class CustomAuthenticationWebFilter {
    @Bean
    fun authenticationWebFilter(
        authenticationManager: ReactiveAuthenticationManager,
        jwtAuthenticationConverter: JwtAuthenticationConverter
    ): AuthenticationWebFilter {
        return AuthenticationWebFilter(authenticationManager).apply {
            setServerAuthenticationConverter(jwtAuthenticationConverter)
        }
    }
}

@Component
class CustomAuthenticationManager : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> = mono {
        UsernamePasswordAuthenticationToken(authentication.principal, "", listOf())
    }
}