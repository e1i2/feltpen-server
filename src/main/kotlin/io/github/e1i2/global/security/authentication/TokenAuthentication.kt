package io.github.e1i2.global.security.authentication

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.security.Key

class TokenAuthentication(
    private val secretKey: String,
    token: String
) {
    private val jwtToken = token.removeBearerPrefix()

    fun parseTokenOrThrowException(): Jws<Claims> {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        val key: Key = Keys.hmacShaKeyFor(keyBytes)
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jwtToken)
    }

    private fun String.removeBearerPrefix(): String {
        return replace("Bearer ", "", true)
    }
}