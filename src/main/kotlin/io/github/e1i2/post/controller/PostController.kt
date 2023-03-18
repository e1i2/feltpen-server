package io.github.e1i2.post.controller

import io.github.e1i2.post.service.PostAndWorkspaceMember
import io.github.e1i2.post.service.PostService
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
    suspend fun createNew(@PathVariable workspaceId: Long, @RequestBody request: PostCreateRequest): Long {
        return postService.savePost(request.title, workspaceId).id
    }

    @GetMapping("/{workspaceId}")
    suspend fun getWorkspacePosts(@PathVariable workspaceId: Long): List<PostAndWorkspaceMember> {
        return postService.getWorkspacePosts(workspaceId)
    }

    @PutMapping("/{postId}")
    suspend fun putPostContent(@PathVariable postId: Long) {
    }
}

data class PostCreateRequest(
    val title: String = "Untitled"
)
