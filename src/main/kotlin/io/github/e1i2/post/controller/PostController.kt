package io.github.e1i2.post.controller

import com.fasterxml.jackson.databind.JsonNode
import io.github.e1i2.post.Status
import io.github.e1i2.post.service.PostAndWorkspaceMember
import io.github.e1i2.post.service.PostService
import java.time.LocalDateTime
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PostController(
    private val postService: PostService
) {
    @PostMapping("/api-public/feltpen/workspaces/{workspaceId}/posts/{folderId}")
    suspend fun saveNewPost(
        @PathVariable workspaceId: Long,
        @PathVariable folderId: Long
    ): Long {
        return postService.savePost(workspaceId, folderId).id
    }

    @PutMapping("/api-public/feltpen/workspaces/posts/{postId}")
    suspend fun updatePost(
        @PathVariable postId: Long,
        @RequestBody request: PostUpdateRequest
    ): Long {
        return postService.updatePost(request.title, request.content, request.status, postId).id
    }

    @GetMapping("/api-public/feltpen/workspaces/posts/{postId}")
    suspend fun getPost(@PathVariable postId: Long): PostResponse {
        val post = postService.getPostById(postId)
        return PostResponse(
            id = post.id,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
            content = post.content,
            title = post.title,
            status = post.toString()
        )
    }

    @GetMapping("/api-public/feltpen/workspaces/{workspaceId}/posts")
    suspend fun getWorkspacePosts(@PathVariable workspaceId: Long): WorkspaceListResponse {
        val postAndMembers = postService.getWorkspacePosts(workspaceId)
        return WorkspaceListResponse(postAndMembers)
    }

    @GetMapping("/api-public/feltpen/workspaces/{workspaceId}/posts/{folderId}")
    suspend fun getPostsInFolder(
        @PathVariable workspaceId: Long,
        @PathVariable folderId: Long
    ): WorkspaceListResponse {
        val postAndMembers = postService.getWorkspacePosts(workspaceId)
        return WorkspaceListResponse(postAndMembers)
    }
}

data class PostUpdateRequest(
    val title: String,
    val content: JsonNode,
    val status: Status
)

data class PostResponse(
    val id: Long,
    val content: JsonNode,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val status: String
)

data class WorkspaceListResponse(
    val posts: List<PostAndWorkspaceMember>
)
