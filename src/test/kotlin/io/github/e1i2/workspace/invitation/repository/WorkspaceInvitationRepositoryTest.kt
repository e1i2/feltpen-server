package io.github.e1i2.workspace.invitation.repository

import io.github.e1i2.user.TestUtils.withTransactionRollback
import io.github.e1i2.workspace.invitation.WorkspaceInvitation
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.transaction.reactive.TransactionalOperator

@DataR2dbcTest
class WorkspaceInvitationRepositoryTest(
    private val workspaceInvitationRepository: WorkspaceInvitationRepository,
    private val transactionalOperator: TransactionalOperator
): StringSpec() {
    init {
        "Workspace 초대 저장 테스트" {
            withTransactionRollback(transactionalOperator) {
                val workspaceInvitation = WorkspaceInvitation(email = "test@gmail.com", workspaceId = 1, code = "test", expireAt = LocalDateTime.now())
                val result = workspaceInvitationRepository.save(workspaceInvitation)
                result.id shouldNotBe 0
            }
        }
    }
}