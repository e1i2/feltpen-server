package io.github.e1i2.user

import java.time.LocalDateTime
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

object TestUtils {
    fun buildUser(
        id: Long = 0,
        name: String = "name",
        email: String = "email@gmail.com"
    ) = User(
        id = id,
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
