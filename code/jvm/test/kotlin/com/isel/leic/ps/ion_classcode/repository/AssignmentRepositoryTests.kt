package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiAssignmentRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.sql.Timestamp
import java.time.Instant

class AssignmentRepositoryTests {
    @Test
    fun `can create a assignment`() = testWithHandleAndRollback { handle ->
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val classroomId = 1
        assignmentRepo.createAssignment(
            assignment = AssignmentInput(
                classroomId = classroomId,
                maxElemsPerGroup = 10,
                maxNumberGroups = 10,
                releaseDate = Timestamp.from(Instant.now()),
                description = "aaaa",
                title = "aaa",
            ),
        )
    }

    @Test
    fun `can get a assignment`() = testWithHandleAndRollback { handle ->
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val assignmentId = 1
        val title = "title"
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId) ?: fail("Assignment not found")
        assert(assignment.title == title)
    }

    @Test
    fun `can delete a assignment`() = testWithHandleAndRollback { handle ->
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val assignmentId = 3
        assignmentRepo.deleteAssignment(assignmentId = assignmentId)
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId)
        assert(assignment == null)
    }

    @Test
    fun `cannot delete a assignment`() = testWithHandleAndRollback { handle ->
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val assignmentId = 1
        try {
            assignmentRepo.deleteAssignment(assignmentId = assignmentId)
            fail("Should not be able to delete assignment")
        } catch (e: Exception) {
            assert(true)
        }
    }

    @Test
    fun `can update a assignment title`() = testWithHandleAndRollback { handle ->
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val newTitle = "title changed"
        val assignmentId = 1
        assignmentRepo.updateAssignmentTitle(assignmentId = assignmentId, title = newTitle)
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId) ?: fail("Assignment not found")
        assert(assignment.title == newTitle)
    }

    @Test
    fun `can update a assignment description`() = testWithHandleAndRollback { handle ->
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val assignmentId = 1
        val newDescription = "description changed"
        assignmentRepo.updateAssignmentDescription(assignmentId = assignmentId, description = newDescription)
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId) ?: fail("Assignment not found")
        assert(assignment.description == newDescription)
    }

    @Test
    fun `can update a assignment max groups`() = testWithHandleAndRollback { handle ->
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val assignmentId = 1
        val newGroupsMax = 12
        assignmentRepo.updateAssignmentNumbGroups(assignmentId = assignmentId, numb = newGroupsMax)
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId) ?: fail("Assignment not found")
        assert(assignment.maxNumberGroups == newGroupsMax)
    }

    @Test
    fun `can update a assignment max elems`() = testWithHandleAndRollback { handle ->
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val assignmentId = 1
        val newElementsMax = 3
        assignmentRepo.updateAssignmentNumbElemsPerGroup(assignmentId = assignmentId, numb = newElementsMax)
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId) ?: fail("Assignment not found")
        assert(assignment.maxElemsPerGroup == newElementsMax)
    }
}
