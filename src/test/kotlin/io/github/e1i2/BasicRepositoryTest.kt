package io.github.e1i2

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOneOrNull

@DataR2dbcTest
class BasicRepositoryTest(
    private val databaseClient: DatabaseClient
): ShouldSpec({
    should("DB에서 쿼리 조회 결과를 가져올 수 있다") {
        val result = databaseClient.sql("SELECT 'hello'").fetch().awaitOneOrNull()
        result?.get("'hello'") shouldBe "hello"
    }
})
