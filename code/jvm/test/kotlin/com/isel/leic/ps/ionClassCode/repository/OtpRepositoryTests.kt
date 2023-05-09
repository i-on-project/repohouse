package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.input.OtpInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiOtpRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class OtpRepositoryTests {

    @Test
    fun `can create otp request`() = testWithHandleAndRollback { handle ->
        val otpRepo = JdbiOtpRepository(handle)
        val userId = 3
        val optInput = OtpInput(userId = userId, otp = 123456)
        otpRepo.createOtpRequest(optInput)
        val otp = otpRepo.getOtpRequest(userId = userId)
        assert(otp != null)
    }

    @Test
    fun `can get otp request`() = testWithHandleAndRollback { handle ->
        val otpRepo = JdbiOtpRepository(handle)
        val userId = 4
        val otp = otpRepo.getOtpRequest(userId = userId)
        assert(otp != null)
    }

    @Test
    fun `can not get otp request`() = testWithHandleAndRollback { handle ->
        val otpRepo = JdbiOtpRepository(handle)
        val userId = 2
        val otp = otpRepo.getOtpRequest(userId = userId)
        assert(otp == null)
    }

    @Test
    fun `add tries to otp request`() = testWithHandleAndRollback { handle ->
        val otpRepo = JdbiOtpRepository(handle)
        val userId = 5
        val tries = otpRepo.getOtpRequest(userId = userId)?.tries ?: fail { "Otp should exist" }
        assert(otpRepo.addTryToOtpRequest(userId = userId, tries = tries))
    }

    @Test
    fun `can delete otp request`() = testWithHandleAndRollback { handle ->
        val otpRepo = JdbiOtpRepository(handle)
        val userId = 5
        otpRepo.deleteOtpRequest(userId = userId)
        val opt = otpRepo.getOtpRequest(userId = userId)
        assert(opt == null)
    }
}
