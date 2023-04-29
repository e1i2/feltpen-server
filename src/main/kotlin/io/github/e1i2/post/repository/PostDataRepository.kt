package io.github.e1i2.post.repository

import io.github.e1i2.post.PostData
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostDataRepository: CoroutineCrudRepository<PostData, Long> {
    @Query("""SELECT * FROM post_data WHERE post_id = :postId ORDER BY id DESC LIMIT 1""")
    suspend fun findLatestByPostId(postId: Long): PostData?
}
