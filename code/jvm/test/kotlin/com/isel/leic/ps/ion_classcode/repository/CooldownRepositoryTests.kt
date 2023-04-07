package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiCooldownRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.sql.Timestamp
import java.time.Instant

class CooldownRepositoryTests {

    @Test
    fun `can create a cooldown request`() = testWithHandleAndRollback { handle ->
        val cooldownRepo = JdbiCooldownRepository(handle)
        val userId = 1
        cooldownRepo.createCooldownRequest(userId = userId, endTime = Timestamp.from(Instant.now()))
    }

    @Test
    fun `can get cooldown request`() = testWithHandleAndRollback { handle ->
        val cooldownRepo = JdbiCooldownRepository(handle)
        val userId = 1
        cooldownRepo.getCooldownRequest(userId = userId) ?: fail("No cooldown request found")
    }

    @Test
    fun `can delete a cooldown request`() = testWithHandleAndRollback { handle ->
        val cooldownRepo = JdbiCooldownRepository(handle)
        val userId = 1
        cooldownRepo.deleteCooldownRequest(userId = userId)
        val id = cooldownRepo.getCooldownRequest(userId = userId)
        assert(id == null)
    }
}
