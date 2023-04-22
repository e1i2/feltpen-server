package io.github.e1i2.workspace.repository

import io.github.e1i2.workspace.Workspace
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkspaceRepository: CoroutineCrudRepository<Workspace, Long> {
    @Query("""SELECT workspace.* FROM workspace
        INNER JOIN workspace_member ON workspace.id = workspace_member.workspace_id
        WHERE workspace_member.user_id = :userId AND workspace.deleted_at IS NULL""")
    suspend fun findAllByWorkspaceMemberId(userId: Long): List<Workspace>
}
