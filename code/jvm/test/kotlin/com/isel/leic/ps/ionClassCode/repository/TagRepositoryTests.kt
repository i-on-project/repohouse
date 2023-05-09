package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.input.TagInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiTagRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.sql.Timestamp
import java.time.Instant

class TagRepositoryTests {

    @Test
    fun `can create a tag`() = testWithHandleAndRollback { handle ->
        val tagRepo = JdbiTagRepository(handle = handle)
        val deliveryId = 2
        val repoId = 2
        val created = tagRepo.createTag(tag = TagInput(name = "name", isDelivered = false, tagDate = Timestamp.from(Instant.now()), deliveryId = deliveryId, repoId = repoId))
        val tag = tagRepo.getTagById(tagId = created.id)
        assert(tag != null)
    }

    @Test
    fun `can get a tag by id`() = testWithHandleAndRollback { handle ->
        val tagRepo = JdbiTagRepository(handle = handle)
        val tagId = 1
        val name = "tag1"
        val tag = tagRepo.getTagById(tagId = tagId) ?: fail("Tag not found")
        assert(tag.name == name)
    }

    @Test
    fun `can delete a tag`() = testWithHandleAndRollback { handle ->
        val tagRepo = JdbiTagRepository(handle = handle)
        val tagId = 1
        tagRepo.deleteTag(tagId = tagId)
        val tag = tagRepo.getTagById(tagId = tagId)
        assert(tag == null)
    }

    @Test
    fun `can get tags by deliveryId`() = testWithHandleAndRollback { handle ->
        val tagRepo = JdbiTagRepository(handle = handle)
        val deliveryId = 1
        val list = tagRepo.getTagsByDelivery(deliveryId = deliveryId)
        assert(list.size == 3)
    }
}
