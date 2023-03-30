package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Outbox
import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput
import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import java.sql.Timestamp

interface CooldownRepository {
    fun createCooldownRequest(userId:Int,end_time:Timestamp): Int?
    fun getCooldownRequest(userId: Int): Boolean
    fun deleteCooldownRequest(userId: Int): Boolean

}
