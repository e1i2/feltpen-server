package io.github.e1i2.user.verificationcode.repository

import io.github.e1i2.user.verificationcode.VerificationCode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class VerificationCodeTest: StringSpec() {
    init {
        val expiredVerificationCode = VerificationCode(
            email = "email@gmail.com",
            code = "code",
            createdAt = LocalDateTime.now().minusMinutes(30)
        )

        val verificationCode = VerificationCode(
            email = "email@gmail.com",
            code = "code"
        )

        "유효한 VerificationCode 테스트" {
            verificationCode.isExpired() shouldBe false
        }

        "만료된 VerificationCode 테스트" {
            expiredVerificationCode.isExpired() shouldBe true
        }
    }
}