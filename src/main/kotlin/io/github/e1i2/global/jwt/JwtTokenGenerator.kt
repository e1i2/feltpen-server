package io.github.e1i2.global.jwt

import io.github.e1i2.utils.toDate
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtTokenGenerator(
    @Value("\${jwt.secretkey}")
    private val secretKey: String
) : TokenGenerator {
    override fun generate(subject: String, expireAt: LocalDateTime): String {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        val key: Key = Keys.hmacShaKeyFor(keyBytes)

        return Jwts.builder()
            .setSubject(subject)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(expireAt.toDate())
            .compact()
    }
}