package io.github.e1i2.global.security.jwt

import java.time.LocalDateTime

interface TokenGenerator {
    fun generate(subject: String, expireAt: LocalDateTime): String
}
