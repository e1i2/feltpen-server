package io.github.e1i2.post.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.e1i2.folder.service.FolderService
import io.github.e1i2.global.security.authentication.AuthenticationService
import io.github.e1i2.post.Post
import io.github.e1i2.post.PostData
import io.github.e1i2.post.Status
import io.github.e1i2.post.repository.PostDataRepository
import io.github.e1i2.post.repository.PostRepository
import io.github.e1i2.workspace.service.WorkspaceService
import java.time.LocalDateTime
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PostService(
    private val postRepository: PostRepository,
    private val postDataRepository: PostDataRepository,
    private val folderService: FolderService,
    private val authenticationService: AuthenticationService,
    private val workspaceService: WorkspaceService,
    private val objectMapper: ObjectMapper
) {
    suspend fun savePost(workspaceId: Long, folderId: Long): Post {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        val workspaceMember = workspaceService.getWorkspaceMemberOrNull(workspaceId, currentUserId)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not member of workspace")

        val post = Post(
            status = Status.DRAFT,
            folderId = folderId,
        )
        val savedPost = postRepository.save(post)

        val postData = PostData(
            title = "",
            content = objectMapper.createObjectNode(),
            postId = savedPost.id,
            writerId = workspaceMember.id
        )

        postDataRepository.save(postData)

        return post
    }

    suspend fun updatePost(title: String, content: JsonNode, status: Status, postId: Long): PostData {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        val post = postRepository.findById(postId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")
        val folder = folderService.getFolderOrNull(post.folderId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found")

        checkWorkspaceMemberOrThrow(folder.workspaceId, currentUserId)

        val postData = PostData(
            title = title,
            content = objectMapper.createObjectNode(),
            postId = post.id,
            writerId = currentUserId
        )

        return postDataRepository.save(postData)
    }

    suspend fun getPostById(postId: Long): PostWithLatestData {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        val post = postRepository.findById(postId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")
        val folder = folderService.getFolderOrNull(post.folderId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found")
        val postData = postDataRepository.findLatestByPostId(post.id)
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There's no post data")

        checkWorkspaceMemberOrThrow(folder.workspaceId, currentUserId)
        return PostWithLatestData(
            id = post.id,
            content = postData.content,
            title = postData.title,
            createdAt = post.createdAt,
            updatedAt = postData.createdAt
        )
    }

    suspend fun getWorkspacePosts(workspaceId: Long): List<PostAndWorkspaceMember> {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        checkWorkspaceMemberOrThrow(workspaceId, currentUserId)

        val posts = postRepository.findAllByFolderId(workspaceId)
        val postDataByPostId = posts.associate { post -> post.id to postDataRepository.findLatestByPostId(post.id) }
        val writersByUserId = workspaceService.getAllWorkspaceMember(workspaceId)
            .associateBy { it.userId }

        return posts.map { post ->
            // 항상 존재해야 한다
            val postData = postDataByPostId[post.id]!!
            val writer = writersByUserId[postData.writerId]!!

            PostAndWorkspaceMember(
                id = post.id,
                title = postData.title,
                createdAt = post.createdAt,
                updatedAt = postData.createdAt,
                status = post.status.toString(),
                writerProfileImage = writer.profileImage,
                writerName = writer.name
            )
        }
    }

    private suspend fun checkWorkspaceMemberOrThrow(workspaceId: Long, userId: Long) {
        if (!workspaceService.isWorkspaceMember(workspaceId, userId)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not member of workspace")
        }
    }
}

data class PostAndWorkspaceMember(
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val writerName: String,
    val writerProfileImage: String?,
    val status: String
)

data class PostWithLatestData(
    val id: Long,
    val title: String,
    val content: JsonNode,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)