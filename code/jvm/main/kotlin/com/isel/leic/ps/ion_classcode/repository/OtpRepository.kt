package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Otp
import com.isel.leic.ps.ion_classcode.domain.input.OtpInput

/**
 * Repository functions for Outbox Repository
 */
interface OtpRepository {

    fun createOtpRequest(otp: OtpInput): Otp
    fun getOtpRequest(userId: Int): Otp?
    fun addTryToOtpRequest(userId: Int, tries: Int): Boolean
    fun deleteOtpRequest(userId: Int)

}
