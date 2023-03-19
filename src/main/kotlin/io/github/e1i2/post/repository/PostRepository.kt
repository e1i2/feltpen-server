package io.github.e1i2.post.repository

import io.github.e1i2.post.Post
import io.github.e1i2.post.service.PostAndWorkspaceMember
import java.time.LocalDateTime
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostRepository: CoroutineCrudRepository<Post, Long> {
    @Query("""
        SELECT post.title, post.status, post.created_at, post.updated_at, workspace_member.name AS writer_name, workspace_member.profile_image AS writer_profile_image
        FROM post
        INNER JOIN workspace_member ON post.writer_id = workspace_member.id
        WHERE post.workspace_id = :workspaceId
    """)
    suspend fun findAllWorkspacePostsAndWriters(workspaceId: Long): List<PostAndWorkspaceMember>
}
