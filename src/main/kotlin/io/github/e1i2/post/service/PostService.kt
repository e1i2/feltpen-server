package io.github.e1i2.post.service

import com.fasterxml.jackson.databind.JsonNode
import io.github.e1i2.global.security.authentication.AuthenticationService
import io.github.e1i2.post.Post
import io.github.e1i2.post.Status
import io.github.e1i2.post.block.repository.BlockRepository
import io.github.e1i2.post.repository.PostRepository
import io.github.e1i2.workspace.service.WorkspaceService
import java.time.LocalDateTime
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PostService(
    private val blockRepository: BlockRepository,
    private val postRepository: PostRepository,
    private val authenticationService: AuthenticationService,
    private val workspaceService: WorkspaceService
) {
    suspend fun savePost(title: String, content: JsonNode, workspaceId: Long, postId: Long?): Post {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        checkWorkspaceMemberOrThrow(workspaceId, currentUserId)

        val post = Post(
            title = title,
            workspaceId = workspaceId,
            writerId = currentUserId,
            status = Status.PENDING,
            content = content,
            id = postId ?: 0
        )
        return postRepository.save(post)
    }

    suspend fun getPostById(postId: Long): Post {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        val post = postRepository.findById(postId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")

        checkWorkspaceMemberOrThrow(post.workspaceId, currentUserId)
        return post
    }

    suspend fun getWorkspacePosts(workspaceId: Long): List<PostAndWorkspaceMember> {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        checkWorkspaceMemberOrThrow(workspaceId, currentUserId)

        return postRepository.findAllWorkspacePostsAndWriters(workspaceId)
    }

    private suspend fun checkWorkspaceMemberOrThrow(workspaceId: Long, userId: Long) {
        if (!workspaceService.isWorkspaceMember(workspaceId, userId)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not member of workspace")
        }
    }
}

data class PostAndWorkspaceMember(
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val writerName: String,
    val writerProfileImage: String?,
    val status: String
)