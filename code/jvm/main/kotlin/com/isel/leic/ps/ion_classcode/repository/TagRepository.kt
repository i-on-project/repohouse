package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Tags
import com.isel.leic.ps.ion_classcode.domain.input.TagInput

interface TagRepository {
    fun createTag(tag: TagInput): Int
    fun deleteTag(tagId: Int)
    fun getTagById(tagId: Int): Tags
    fun getTagsByDelivery(deliveryId: Int): List<Tags>
}

