package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.CompositeInput
import com.isel.leic.ps.ion_classcode.domain.requests.Composite

interface CompositeRepository {
    fun createCompositeRequest(request: CompositeInput): Int
    fun changeStateCompositeRequest(id: Int, state: String)
    fun getCompositeRequests(): List<Composite>
    fun getCompositeRequestById(id: Int): Composite?
    fun getCompositeRequestsByUser(userId: Int): List<Composite>
}
