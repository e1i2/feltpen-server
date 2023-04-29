package io.github.e1i2.folder.repository

import io.github.e1i2.folder.Folder
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FolderRepository: CoroutineCrudRepository<Folder, Long> {
    suspend fun findAllByWorkspaceIdOrderByCreatedAt(workspaceId: Long): List<Folder>
}
