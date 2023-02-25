package io.github.e1i2.user.verificationcode.repository

import io.github.e1i2.user.verificationcode.VerificationCode
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface VerificationCodeRepository: CoroutineCrudRepository<VerificationCode, String> {
    @Query("""SELECT * FROM verification_code WHERE code = :code AND email = :email""")
    suspend fun findValidVerificationCodeByCodeAndEmail(code: String, email: String): VerificationCode?
}
