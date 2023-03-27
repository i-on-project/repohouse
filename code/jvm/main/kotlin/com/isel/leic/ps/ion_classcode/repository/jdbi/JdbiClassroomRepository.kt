package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.repository.ClassroomRepository
import org.jdbi.v3.core.Handle

class JdbiClassroomRepository(private val handle: Handle) : ClassroomRepository {
    override fun createClassroom(classroom: ClassroomInput): Int {
        return handle.createUpdate(
            """
            INSERT INTO Classroom (name, last_sync, invite_link,is_archive,course_id)
            VALUES (:name,CURRENT_DATE,:invite_link,false,:course_id)
            RETURNING id
            """,
        )
            .bind("name", classroom.name)
            .bind("invite_link", classroom.inviteLink)
            .bind("course_id", classroom.courseId)
            .execute()
    }

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
            .mapTo(Classroom::class.java)
            .firstOrNull()
    }

    override fun getAssigmentsOfAClassroom(classroomId: Int): List<Assigment> {
        return handle.createQuery(
            """
            SELECT * FROM assignment
            WHERE classroom_id = :classroom_id
            """,
        )
            .bind("classroom_id", classroomId)
            .mapTo(Assigment::class.java)
            .list()
    }
}
