package com.isel.leic.ps.ion_classcode.repository

import java.sql.Timestamp

interface CooldownRepository {
    fun createCooldownRequest(userId: Int, endTime: Timestamp): Int?
    fun getCooldownRequest(userId: Int): Int?
    fun deleteCooldownRequest(userId: Int): Boolean
}