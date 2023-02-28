package io.github.e1i2.user.verificationcode

import io.github.e1i2.utils.getRandomString
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "verification_code")
data class VerificationCode(
    @Id
    val id: Long = 0,
    val code: String = getRandomString(15),
    val email: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expireAt: LocalDateTime = createdAt.plusMinutes(30),
    val isUsed: Boolean = false
) {
    fun isExpired(): Boolean {
        return expireAt.isBefore(LocalDateTime.now())
    }

    fun markAsUsed() =
        copy(isUsed = true)
}
