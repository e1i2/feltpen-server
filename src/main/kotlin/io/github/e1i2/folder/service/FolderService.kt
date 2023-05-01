package io.github.e1i2.folder.service

import io.github.e1i2.folder.Folder
import io.github.e1i2.folder.repository.FolderRepository
import io.github.e1i2.folder.toTree
import org.springframework.stereotype.Service

@Service
class FolderService(
    private val folderRepository: FolderRepository
) {
    suspend fun getAllFoldersWithType(workspaceId: Long, name: String) {
        val folders = folderRepository.findAllByWorkspaceIdOrderByCreatedAt(workspaceId)
        val folderTree = folders.toTree()

        folderTree.findChildByName(name)
    }

    suspend fun saveRootFolder(workspaceId: Long, writerId: Long) {
        folderRepository.save(
            Folder(
                parentFolderId = null,
                name = "",
                workspaceId = workspaceId,
                writerId = writerId,
            )
        )
    }

    suspend fun getFolderOrNull(folderId: Long): Folder? {
        return folderRepository.findById(folderId)
    }
}