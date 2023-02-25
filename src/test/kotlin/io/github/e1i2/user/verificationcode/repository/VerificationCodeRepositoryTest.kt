package io.github.e1i2.user.verificationcode.repository

import io.github.e1i2.user.verificationcode.VerificationCode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest

@DataR2dbcTest
class VerificationCodeRepositoryTest(
    private val verificationCodeRepository: VerificationCodeRepository
) : StringSpec() {
    init {
        "VerificationCode에 저장할 수 있어야 한다" {
            val now = LocalDateTime.now()
            val verificationCode = VerificationCode(
                code = "code",
                email = "email",
                createdAt = now,
                expireAt = now.plusHours(2)
            )

            val result = verificationCodeRepository.save(verificationCode)

            result.code shouldBe "code"
            result.createdAt shouldBe now
        }
    }
}
