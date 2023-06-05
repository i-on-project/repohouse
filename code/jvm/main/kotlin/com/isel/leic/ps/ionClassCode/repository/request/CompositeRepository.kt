package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.CompositeInput
import com.isel.leic.ps.ionClassCode.domain.requests.Composite

/**
 * Repository functions for Composite Repository
 */
interface CompositeRepository {
    fun createCompositeRequest(request: CompositeInput, creator: Int): Composite
    fun updateCompositeState(compositeId: Int): String
    fun getCompositeRequests(): List<Composite>
    fun getCompositeRequestsThatAreNotAccepted(): List<Composite>
    fun getCompositeRequestsById(compositeId: Int): List<Int>?
    fun getCompositeRequestsByUser(userId: Int): List<Composite>
    fun getCompositeByCompositeId(compositeId: Int): Composite?

}
