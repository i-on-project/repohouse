package com.isel.leic.ps.ionClassCode.repository

import java.sql.Timestamp

/**
 * Repository functions for Cooldown Repository
 */
interface CooldownRepository {
    fun createCooldownRequest(userId: Int, endTime: Timestamp): Int
    fun getCooldownRequestRemainingTime(userId: Int): Int?
    fun deleteCooldownRequest(userId: Int)
}
