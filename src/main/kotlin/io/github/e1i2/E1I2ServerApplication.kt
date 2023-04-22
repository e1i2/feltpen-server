package io.github.e1i2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class E1I2ServerApplication

fun main(args: Array<String>) {
    runApplication<E1I2ServerApplication>(*args)
}
