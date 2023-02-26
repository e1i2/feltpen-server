package io.github.e1i2.workspace.repository

import io.github.e1i2.workspace.Workspace
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkspaceRepository: CoroutineCrudRepository<Workspace, Long>
