package io.github.e1i2.workspace.invitation.repository

import io.github.e1i2.workspace.invitation.WorkspaceInvitation
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkspaceInvitationRepository: CoroutineCrudRepository<WorkspaceInvitation, Long> {
    suspend fun findByWorkspaceIdAndCode(workspaceId: Long, code: String): WorkspaceInvitation?
    suspend fun findAllByWorkspaceId(workspaceId: Long): List<WorkspaceInvitation>
}
