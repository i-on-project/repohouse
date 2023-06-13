package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeTeamRequestsDeserialization

/**
 * Represents a error response body from ClassCode api
 */

data class TeamRequests(
    val needApproval: RequestsThatNeedApproval,
    val requestsHistory: RequestsHistory,
) {
    constructor(deserialization: ClassCodeTeamRequestsDeserialization) : this (
        needApproval = RequestsThatNeedApproval(deserialization = deserialization.needApproval),
        requestsHistory = RequestsHistory(deserialization = deserialization.requestsHistory),
    )
}
