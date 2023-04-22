package io.github.e1i2.user

import io.github.e1i2.user.TestUtils.buildUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec

class UserTest: ShouldSpec({
    context("User 생성") {
        should("email 값이 옳지 않으면 예외가 발생한다") {
            shouldThrow<AssertionError> {
                buildUser(email = "invalidEmail")
            }
        }

        should("name은 3글자 이상이어야 한다") {
            shouldThrow<AssertionError> {
                buildUser(name = "12")
            }
        }
    }
})
