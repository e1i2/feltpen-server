package io.github.e1i2.post.repository

import io.github.e1i2.post.Post
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostRepository: CoroutineCrudRepository<Post, Long> {
    suspend fun findAllByFolderId(folderId: Long): List<Post>
}
