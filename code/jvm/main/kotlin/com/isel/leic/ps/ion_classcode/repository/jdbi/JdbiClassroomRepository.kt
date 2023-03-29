package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.repository.ClassroomRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiClassroomRepository(private val handle: Handle) : ClassroomRepository {
    override fun createClassroom(classroom: ClassroomInput): Int =
        handle.createUpdate(
            """
            INSERT INTO Classroom (name, last_sync, invite_link, is_archived, course_id)
            VALUES (:name,CURRENT_DATE,:invite_link,false,:course_id)
            RETURNING id
            """,
        )
            .bind("name", classroom.name)
            .bind("invite_link", classroom.inviteLink)
            .bind("course_id", classroom.courseId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

    override fun deleteClassroom(classroomId: Int) {
        handle.createUpdate(
            """
            DELETE FROM Classroom
            WHERE id = :id
            """,
        )
            .bind("id", classroomId)
            .execute()
    }

    override fun getClassroomById(classroomId: Int): Classroom? {
        return handle.createQuery(
            """
            SELECT * FROM Classroom
            WHERE id = :id
            """,
        )
            .bind("id", classroomId)
            .mapTo<Classroom>()
            .firstOrNull()
    }

    override fun getAssignmentsOfAClassroom(classroomId: Int): List<Assigment> {
        return handle.createQuery(
            """
            SELECT * FROM assignment
            WHERE classroom_id = :classroom_id
            """,
        )
            .bind("classroom_id", classroomId)
            .mapTo<Assigment>()
            .list()
    }
}
