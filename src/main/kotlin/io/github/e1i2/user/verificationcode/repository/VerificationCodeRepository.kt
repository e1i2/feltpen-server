package io.github.e1i2.user.verificationcode.repository

import io.github.e1i2.user.verificationcode.VerificationCode
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface VerificationCodeRepository: CoroutineCrudRepository<VerificationCode, String>
