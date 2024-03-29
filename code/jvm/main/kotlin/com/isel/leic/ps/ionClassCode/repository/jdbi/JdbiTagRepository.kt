package com.isel.leic.ps.ionClassCode.repository.jdbi

import com.isel.leic.ps.ionClassCode.domain.Tags
import com.isel.leic.ps.ionClassCode.domain.input.TagInput
import com.isel.leic.ps.ionClassCode.repository.TagRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Tag methods
 */
class JdbiTagRepository(private val handle: Handle) : TagRepository {

    /**
     * Method to create a Tag
     */
    override fun createTag(tag: TagInput): Tags {
        val id = handle.createUpdate(
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
        return Tags(id, tag.name, tag.isDelivered, tag.tagDate, tag.deliveryId, tag.repoId)
    }

    /**
     * Method to delete a Tag
     */
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

    /**
     * Method to get a Tag by is id
     */
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

    /**
     * Method to get all Tag's by a delivery
     */
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
