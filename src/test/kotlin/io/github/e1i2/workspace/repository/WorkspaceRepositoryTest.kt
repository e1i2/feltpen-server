package io.github.e1i2.workspace.repository

import io.github.e1i2.user.TestUtils.withTransactionRollback
import io.github.e1i2.workspace.Workspace
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.transaction.reactive.TransactionalOperator

@DataR2dbcTest
class WorkspaceRepositoryTest(
    private val workspaceRepository: WorkspaceRepository,
    private val transactionalOperator: TransactionalOperator
): StringSpec() {
    init {
        "Workspace 저장 테스트" {
            withTransactionRollback(transactionalOperator) {
                val workspace = Workspace(name = "name")
                val savedWorkspace = workspaceRepository.save(workspace)
                savedWorkspace.id shouldNotBe null
            }
        }
    }
}