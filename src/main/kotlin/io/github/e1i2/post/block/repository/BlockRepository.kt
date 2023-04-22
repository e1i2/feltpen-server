package io.github.e1i2.post.block.repository

import io.github.e1i2.post.block.Block
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface BlockRepository: CoroutineCrudRepository<Block, Long> {
    suspend fun findAllByPostId(postId: Long): List<Block>
}
