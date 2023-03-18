package io.github.e1i2.global.exception

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class ExceptionHandlerWebFilter(
    private val objectMapper: ObjectMapper
): WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        println("${exchange.request.method} ${exchange.request.path}")
        return chain.filter(exchange)
            .onErrorResume {
                when (it) {
                    is ResponseStatusException -> it.toErrorResponse(exchange)
                    else -> ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error, ${it.message}")
                        .toErrorResponse(exchange)
                }
            }
    }

    private fun ResponseStatusException.toErrorResponse(exchange: ServerWebExchange): Mono<Void> {
        exchange.response.statusCode = statusCode
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON

        val errorResponse = ExceptionResponse(message, statusCode.value())
        val bytes = objectMapper.writeValueAsBytes(errorResponse)
        val buffer = exchange.response.bufferFactory().wrap(bytes)

        return exchange.response.writeWith(Flux.just(buffer))
    }
}

data class ExceptionResponse(
    val message: String?,
    val statusCode: Int
)
