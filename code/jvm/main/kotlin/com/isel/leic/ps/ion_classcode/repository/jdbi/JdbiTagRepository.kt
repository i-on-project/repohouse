package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Tags
import com.isel.leic.ps.ion_classcode.domain.input.TagInput
import com.isel.leic.ps.ion_classcode.repository.TagRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiTagRepository(private val handle: Handle) : TagRepository {
    override fun createTag(tag: TagInput): Int {
        return handle.createUpdate(
            """
                INSERT INTO tags (name, is_delivered, tag_date, delivery_id,repo_id) 
                VALUES (:name, :is_delivered, :tag_date, :delivery_id,:repoId)
                RETURNING id
                """,
        )
            .bind("name", tag.name)
            .bind("is_delivered", tag.isDelivered)
            .bind("tag_date", tag.tagDate)
            .bind("delivery_id", tag.deliveryId)
            .bind("repoId", tag.repoId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

    override fun deleteTag(tagId: Int) {
        handle.createUpdate(
            """
                DELETE FROM tags
                WHERE id = :tagId
                """,
        )
            .bind("tagId", tagId)
            .execute()
    }

    override fun getTagById(tagId: Int): Tags? {
        return handle.createQuery(
            """
                SELECT * FROM tags
                WHERE id = :tagId
                """,
        )
            .bind("tagId", tagId)
            .mapTo<Tags>()
            .firstOrNull()
    }

    override fun getTagsByDelivery(deliveryId: Int): List<Tags> {
        return handle.createQuery(
            """
                SELECT * FROM tags
                WHERE delivery_id = :deliveryId
                """,
        )
            .bind("deliveryId", deliveryId)
            .mapTo<Tags>()
            .list()
    }
}
