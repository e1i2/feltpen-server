package io.github.e1i2.workspace.member.repository

import io.github.e1i2.workspace.member.WorkspaceMember
import io.github.e1i2.workspace.service.WorkspaceMemberDto
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkspaceMemberRepository: CoroutineCrudRepository<WorkspaceMember, Long> {
    suspend fun findByWorkspaceIdAndUserId(workspaceId: Long, userId: Long): WorkspaceMember?
    @Query("""
        SELECT user.id user_id, workspace_member.id member_id, user.name, user.profile_image, workspace_member.role
        FROM workspace_member
        INNER JOIN user ON workspace_member.user_id = user.id
        WHERE workspace_id = :workspaceId AND workspace_member.deleted_at IS NULL
    """)
    suspend fun findAllMemberByWorkspaceId(workspaceId: Long): List<WorkspaceMemberDto>
}
