package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.requests.CreateTeam
import com.isel.leic.ps.ion_classcode.domain.requests.JoinTeam
import com.isel.leic.ps.ion_classcode.domain.requests.LeaveTeam

data class RequestOutputModel(
    val status: Int,
    val id:Int,
    val title: String
):OutputModel

data class RequestsOutputModel(
    val createTeam: List<CreateTeam>,
    val joinTeam: List<JoinTeam>,
    val leaveTeam: List<LeaveTeam>
):OutputModel

data class RequestCreatedOutputModel(
    val id:Int,
    val created:Boolean
):OutputModel
data class RequestChangeStatusOutputModel(
    val id:Int,
    val changed:Boolean
):OutputModel
