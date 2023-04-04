package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

sealed class TeamServicesError

@Component
class TeamServices(
    val transactionManager: TransactionManager,
) {
    // TODO: getTeamInfo
    // TODO: getTeamRequests
    // TODO: createTeamRequest
    // TODO: joinTeamRequest
    // TODO: exitTeamRequest
    // TODO: updateTeamRequestStatus (rejected -> pending)
}
