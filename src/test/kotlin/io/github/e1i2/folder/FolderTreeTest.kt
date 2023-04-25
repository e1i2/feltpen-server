package io.github.e1i2.folder

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class FolderTreeTest: StringSpec() {
    private val rootFolder = buildFolder(parentNodeId = null, name = "/")
    private val postChildFolder = buildFolder(parentNodeId = rootFolder.id, name = "/post")
    private val postChildChildFolder = buildFolder(parentNodeId = postChildFolder.id, name = "/child")
    private val greetingChildFolder = buildFolder(parentNodeId = rootFolder.id, name = "/greeting")

    private val folders = listOf(rootFolder, postChildFolder, postChildChildFolder, greetingChildFolder)
    private val folderTree = folders.toTree()

    init {
        "폴더 리스트를 트리로 변환 테스트" {
            folderTree.currentValue.id shouldBe rootFolder.id
            folderTree.children.size shouldBe 2
            folderTree.children.first().currentValue.id shouldBe postChildFolder.id
            folderTree.children.first().currentValue.parentFolderId shouldBe postChildFolder.parentFolderId
            folderTree.children.first().children.first().currentValue.id shouldBe postChildChildFolder.id
        }

        "자식 폴더 이름으로 검색" {
            folderTree.findChildByName("/post")?.currentValue?.id shouldBe postChildFolder.id
            folderTree.findChildByName("/greeting")?.currentValue?.id shouldBe greetingChildFolder.id
            folderTree.findChildByName("/child").shouldBeNull()
        }
    }

    private fun buildFolder(id: Long = Random.nextLong(), parentNodeId: Long?, name: String = "/test"): Folder =
        Folder(
            id = id,
            parentFolderId = parentNodeId,
            writerId = 1,
            name = name,
            workspaceId = 1
        )
}