package io.github.e1i2.global.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter


@EnableWebFluxSecurity
@Configuration
class SpringSecurityConfig(
    private val authenticationWebFilter: AuthenticationWebFilter
) {
    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            authorizeExchange {
                authorize("/users/verification-code", permitAll)
                authorize("/users/signin", permitAll)
                authorize(anyExchange, authenticated)
            }

            addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

            csrf {
                disable()
            }

            cors {
                disable()
            }

            formLogin {
                disable()
            }
        }
    }
}
