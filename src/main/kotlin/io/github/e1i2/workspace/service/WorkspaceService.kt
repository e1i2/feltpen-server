package io.github.e1i2.workspace.service

import io.github.e1i2.workspace.Workspace
import io.github.e1i2.workspace.repository.WorkspaceRepository
import org.springframework.stereotype.Service

@Service
class WorkspaceService(
    private val workspaceRepository: WorkspaceRepository
) {
    suspend fun saveWorkspace(name: String) {
        val workspace = Workspace(name = name)
        workspaceRepository.save(workspace)
    }
}