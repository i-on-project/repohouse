package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiClassroomRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ClassroomRepositoryTests {
    @Test
    fun `can create classroom`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = 1, teacherId = 1), inviteLink = "linking link")
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

    @Test
    fun `can update the classroom name`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val classroomId = 1
        val newName = "New Name"
        val classroomUpdate = ClassroomUpdateInputModel(name = newName)
        classroomRepo.updateClassroomName(classroomId = classroomId, classroomUpdate = classroomUpdate)
        val classroom = classroomRepo.getClassroomById(classroomId = classroomId) ?: fail("Classroom not found")
        assert(classroom.name == newName)
    }

    @Test
    fun `can get the assignments the classroom`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val classroomId = 1
        val assignments = classroomRepo.getAssignmentsOfAClassroom(classroomId = classroomId)
        assert(assignments.size == 3)
    }

    @Test
    fun `can archive a classroom`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val classroomId = 1
        classroomRepo.archiveClassroom(classroomId = classroomId)
        val classroom = classroomRepo.getClassroomById(classroomId = classroomId) ?: fail("Classroom not found")
        assert(classroom.isArchived)
    }

    @Test
    fun `can get witch classroom from a course the student is in`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val courseId = 1
        val studentId = 3
        val classroomId = classroomRepo.getStudentClassroomId(courseId = courseId, studentId = studentId) ?: fail("Classroom not found")
        assert(classroomId == 1)
    }

    @Test
    fun `can get the classroom invite link`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val classroomId = 1
        val link = "https://classroom.github.com/a/123"
        val inviteLink = classroomRepo.getClassroomInviteLink(classroomId = classroomId) ?: fail("Classroom not found")
        assert(inviteLink == link)
    }

    @Test
    fun `can get a classroom by classroom invite link`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val link = "https://classroom.github.com/a/123"
        val classroom = classroomRepo.getClassroomByInviteLink(inviteLink = link) ?: fail("Classroom not found")
        assert(classroom.id == 1)
    }

    @Test
    fun `can the all the students in a classroom`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val classroomId = 1
        val students = classroomRepo.getStudentsByClassroom(classroomId = classroomId)
        assert(students.size == 2)
    }

    @Test
    fun `can add a student in a classroom`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val classroomId = 1
        val studentId = 5
        classroomRepo.addStudentToClassroom(classroomId = classroomId, studentId = studentId)
    }

    @Test
    fun `can get all invite links for the classrooms`() = testWithHandleAndRollback { handle ->
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val inviteLinks = classroomRepo.getAllInviteLinks()
        assert(inviteLinks.size == 3)
    }
}
