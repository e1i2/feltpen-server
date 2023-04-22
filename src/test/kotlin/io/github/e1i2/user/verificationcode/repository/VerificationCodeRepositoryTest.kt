package io.github.e1i2.user.verificationcode.repository

import io.github.e1i2.user.TestUtils.withTransactionRollback
import io.github.e1i2.user.verificationcode.VerificationCode
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.transaction.reactive.TransactionalOperator

@DataR2dbcTest
class VerificationCodeRepositoryTest(
    private val verificationCodeRepository: VerificationCodeRepository,
    private val transactionalOperator: TransactionalOperator
) : StringSpec() {
    init {
        val verificationCode = VerificationCode(
            code = "code",
            email = "email"
        )

        val expiredVerificationCode = VerificationCode(
            code = "expiredCode",
            email = "expiredEmail",
            createdAt = LocalDateTime.now().minusMinutes(31)
        )

        "VerificationCode에 저장할 수 있어야 한다" {
            withTransactionRollback(transactionalOperator) {
                shouldNotThrow<Exception> {
                    verificationCodeRepository.save(verificationCode)
                }
            }
        }

        "VerificationCode가 만료된 경우에도 조회되어야 한다" {
            withTransactionRollback(transactionalOperator) {
                verificationCodeRepository.save(expiredVerificationCode)

                val foundVerificationCode = verificationCodeRepository.findVerificationCodeByEmailAndCodeAndIsUsedFalse(
                    expiredVerificationCode.email,
                    expiredVerificationCode.code
                )

                foundVerificationCode shouldNotBe null
            }
        }
    }
}
