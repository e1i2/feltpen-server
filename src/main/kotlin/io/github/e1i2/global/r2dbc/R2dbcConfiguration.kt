package io.github.e1i2.global.r2dbc

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.MySqlDialect


@Configuration
class R2dbcConfiguration {
    @Bean
    fun customConversions(objectMapper: ObjectMapper): R2dbcCustomConversions {
        val converters = listOf(JsonNodeToByteArrayConverter(objectMapper), ByteArrayToJsonNodeConverter(objectMapper))
        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, converters)
    }

}