package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.RequestInput
import com.isel.leic.ps.ion_classcode.domain.requests.Request


/**
 * Repository functions for Requests Repository
 */
interface RequestRepository {
    fun createRequest(request: RequestInput,creator:Int): Int
    fun changeStateRequest(id: Int, state: String)
    fun getRequests(): List<Request>
    fun getRequestById(id: Int): Request?
    fun getRequestsByUser(userId: Int): List<Request>
    fun checkIfIsComposite(id: Int): Boolean
}
