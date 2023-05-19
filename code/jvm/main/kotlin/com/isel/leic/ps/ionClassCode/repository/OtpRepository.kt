package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.Otp
import com.isel.leic.ps.ionClassCode.domain.input.OtpInput

/**
 * Repository functions for Outbox Repository
 */
interface OtpRepository {

    fun createOtpRequest(otp: OtpInput): Otp
    fun getOtpRequest(userId: Int): Otp?
    fun addTryToOtpRequest(userId: Int, tries: Int): Boolean
    fun deleteOtpRequest(userId: Int)
}
