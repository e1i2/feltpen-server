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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/posts")
@RestController
class PostController(
    private val postService: PostService
) {
    @PostMapping("/{workspaceId}")
    suspend fun saveNewPost(
        @PathVariable workspaceId: Long,
        @RequestBody request: PostCreateRequest): Long {
        return postService.savePost(request.title, request.content, workspaceId).id
    }

    @PutMapping("/{workspaceId}/{postId}")
    suspend fun updatePost(
        @PathVariable workspaceId: Long,
        @PathVariable postId: Long,
        @RequestBody request: PostUpdateRequest
    ): Long {
        return postService.updatePost(request.title, request.content, request.status, workspaceId, postId).id
    }

    @GetMapping("/{postId}")
    suspend fun getPost(@PathVariable postId: Long): PostResponse {
        val post = postService.getPostById(postId)
        return PostResponse(
            id = post.id,
            createdAt = post.createdAt,
            updatedAt = post.createdAt,
            content = post.content,
            title = post.title
        )
    }

    @GetMapping("/{workspaceId}")
    suspend fun getWorkspacePosts(@PathVariable workspaceId: Long): List<PostAndWorkspaceMember> {
        return postService.getWorkspacePosts(workspaceId)
    }
}

data class PostCreateRequest(
    val title: String = "Untitled",
    val content: JsonNode
)

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
    val updatedAt: LocalDateTime
)
