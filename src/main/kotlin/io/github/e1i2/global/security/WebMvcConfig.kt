package io.github.e1i2.global.security

import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Component
class WebMvcConfig : WebFluxConfigurer {
    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        corsRegistry.addMapping("/**")
            .allowedMethods("*")
            .allowedOriginPatterns("localhost:3000", "app.feltpen.site")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
