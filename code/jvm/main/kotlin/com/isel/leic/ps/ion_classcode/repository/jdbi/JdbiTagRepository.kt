package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Tags
import com.isel.leic.ps.ion_classcode.domain.input.TagInput
import com.isel.leic.ps.ion_classcode.repository.TagRepository
import org.jdbi.v3.core.Handle

class JdbiTagRepository(private val handle: Handle) : TagRepository {
    override fun createTag(tag: TagInput): Int {
        return handle.createUpdate(
            """
                INSERT INTO tags (name,repo_id) 
                VALUES (:name, :repoId)
                RETURNING id
                """
        )
            .bind("name", tag.name)
            .bind("repoId", tag.repoId)
            .execute()
    }

    override fun deleteTag(tagId: Int) {
        handle.createUpdate(
            """
                DELETE FROM tags
                WHERE id = :tagId
                """
        )
            .bind("tagId", tagId)
            .execute()
    }

    override fun getTagById(tagId: Int): Tags {
        return handle.createQuery(
            """
                SELECT * FROM tags
                WHERE id = :tagId
                """
        )
            .bind("tagId", tagId)
            .mapTo(Tags::class.java)
            .one()
    }

    override fun getTagsByDelivery(deliveryId: Int): List<Tags> {
        return handle.createQuery(
            """
                SELECT * FROM tags
                WHERE repo_id = :deliveryId
                """
        )
            .bind("deliveryId", deliveryId)
            .mapTo(Tags::class.java)
            .list()
    }
}
