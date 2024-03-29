package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.RequestInput
import com.isel.leic.ps.ionClassCode.domain.requests.Request

/**
 * Repository functions for Requests Repository
 */
interface RequestRepository {
    fun createRequest(request: RequestInput, creator: Int): Request
    fun changeStateRequest(id: Int, state: String)
    fun getRequests(): List<Request>
    fun getRequestById(id: Int): Request?
    fun getRequestsByUser(userId: Int): List<Request>
    fun checkIfIsComposite(id: Int): Boolean
}
