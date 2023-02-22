package io.github.e1i2.user.repository

import io.github.e1i2.user.TestUtils.buildUser
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest

@DataR2dbcTest
class UserRepositoryTest(
    private val userRepository: UserRepository
): ShouldSpec({
    should("User 저장 성공 테스트") {
        val savedUser = userRepository.save(buildUser())

        savedUser.id shouldNotBe 0
        savedUser.createdAt shouldBeBefore LocalDateTime.now()
        savedUser.deletedAt shouldBe null
    }
})
