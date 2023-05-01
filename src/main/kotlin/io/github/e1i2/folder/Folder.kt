package io.github.e1i2.folder

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Table("folder")
data class Folder(
    @Id
    val id: Long = 0,
    val name: String,
    val parentFolderId: Long?, // null인경우 root node를 의미한다
    val writerId: Long,
    val workspaceId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
)

data class FolderTree(
    val currentValue: Folder,
    val children: List<FolderTree>
) {
    fun findChildByName(name: String): FolderTree? {
        return children.firstOrNull { it.currentValue.name == name }
    }
}

fun List<Folder>.toTree(): FolderTree {
    val foldersByParentFolderId = groupBy { it.parentFolderId }
    val rootFolder = foldersByParentFolderId[null]?.firstOrNull()
        ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Root 폴더가 하나도 없습니다")

    return rootFolder.getChildrenNodes(foldersByParentFolderId)
}

fun Folder.getChildrenNodes(foldersByParentFolderId: Map<Long?, List<Folder>>): FolderTree {
    val childrenFolders = foldersByParentFolderId[id] ?: listOf()

    val childrenNodes = childrenFolders.map {
        it.getChildrenNodes(foldersByParentFolderId)
    }

    return FolderTree(this, childrenNodes)
}

enum class ServiceFolders(
    val folderName: String
) {
    USER_GUIDE("/user-guide")
}
