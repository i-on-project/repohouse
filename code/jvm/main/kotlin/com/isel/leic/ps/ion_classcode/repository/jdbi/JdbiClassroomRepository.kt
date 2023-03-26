package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.domain.input.TeamInput
import com.isel.leic.ps.ion_classcode.repository.ClassroomRepository
import org.jdbi.v3.core.Handle

class JdbiClassroomRepository(private val handle: Handle): ClassroomRepository {
    override fun createClassroom(classroom: ClassroomInput): Int {
        handle.createUpdate(
            """
            INSERT INTO Classroom (name, teacher_id)
            VALUES (:name, :teacherId)
            RETURNING id
            """,
        )
            .bind("name", classroom.name)
            .bind("teacherId", classroom.teacherId)
            .execute()
    }

    override fun deleteClassroom(classroomId: Int) {
        TODO("Not yet implemented")
    }

    override fun enterClassroom(classroomId: Int, userId: Int) {
        TODO("Not yet implemented")
    }

    override fun leaveClassroom(classroomId: Int, userId: Int) {
        TODO("Not yet implemented")
    }

    override fun getClassroomById(classroomId: Int): Classroom? {
        TODO("Not yet implemented")
    }

    override fun getTeamsOfAClassroom(classroomId: Int): List<TeamInput> {
        TODO("Not yet implemented")
    }

}