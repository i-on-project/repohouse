package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiClassroomRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ClassroomRepositoryTests {
    @Test
    fun `can create classroom`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = 1, inviteLink = "linking link", teacherId = 1))
    }

    @Test
    fun `can get the classroom by id`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val classroomId = 1
        val name = "DAW-2223v-LI51D"
        val classroom = classroomRepo.getClassroomById(classroomId = classroomId) ?: fail("Classroom not found")
        assert(classroom.name == name)
    }

    @Test
    fun `can delete classroom`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val classroomId = 2
        classroomRepo.deleteClassroom(classroomId = classroomId)
    }

    @Test
    fun `cannot delete classroom`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val classroomId = 1
        try {
            classroomRepo.deleteClassroom(classroomId = classroomId)
            fail("Should not be able to delete classroom")
        } catch (e: Exception) {
            assert(true)
        }
    }

    @Test
    fun `can get the classroom assignments`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val classroomId = 1
        val classroom = classroomRepo.getClassroomById(classroomId = classroomId)
        assert(classroom != null)
    }
}
