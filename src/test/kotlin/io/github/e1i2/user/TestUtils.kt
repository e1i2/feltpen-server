package io.github.e1i2.user

import java.time.LocalDateTime
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

object TestUtils {
    fun buildUser(
        name: String = "name",
        email: String = "email@gmail.com"
    ) = User(
        name = name,
        email = email,
        createdAt = LocalDateTime.now()
    )

    suspend fun withTransactionRollback(transactionalOperator: TransactionalOperator, block: suspend () -> Unit) {
        transactionalOperator.executeAndAwait {
            block()
            it.setRollbackOnly()
        }
    }
}
