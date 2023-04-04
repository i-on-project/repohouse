package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ion_classcode.repository.ClassroomRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiClassroomRepository(private val handle: Handle) : ClassroomRepository {
    override fun createClassroom(classroom: ClassroomInput,inviteLink: String): Int =
        handle.createUpdate(
            """
            INSERT INTO Classroom (name, last_sync, invite_link, is_archived, course_id)
            VALUES (:name,CURRENT_DATE,:invite_link,false,:course_id)
            RETURNING id
            """,
        )
            .bind("name", classroom.name)
            .bind("invite_link", inviteLink)
            .bind("course_id", classroom.courseId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

    override fun updateClassroomName(classroomId: Int, classroomUpdate: ClassroomUpdateInputModel) {
        handle.createUpdate(
            """
            UPDATE Classroom
            SET name = :name
            WHERE id = :id
            """,
        )
            .bind("name", classroomUpdate.name)
            .bind("id", classroomId)
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

    override fun archiveClassroom(classroomId: Int) {
        handle.createUpdate(
            """
            UPDATE Classroom
            SET is_archived = true
            WHERE id = :id
            """,
        )
            .bind("id", classroomId)
            .execute()
    }

    override fun getStudentClassroomId(courseId: Int, studentId: Int): Int? {
        return handle.createQuery(
            """
            SELECT classroom.id FROM classroom
            INNER JOIN course ON course.id = classroom.course_id
            INNER JOIN student_course ON student_course.course = course.id
            WHERE student_course.student = :student_id AND course.id = :course_id
            """,
        )
            .bind("student_id", studentId)
            .bind("course_id", courseId)
            .mapTo<Int>()
            .firstOrNull()
    }

    override fun getClassroomInviteLink(classroomId: Int): String? {
        return handle.createQuery(
            """
            SELECT invite_link FROM Classroom
            WHERE id = :id
            """,
        )
            .bind("id", classroomId)
            .mapTo<String>()
            .firstOrNull()
    }

    override fun getClassroomByInviteLink(inviteLink: String): Classroom? {
        return handle.createQuery(
            """
            SELECT * FROM Classroom
            WHERE invite_link = :invite_link
            """,
        )
            .bind("invite_link", inviteLink)
            .mapTo<Classroom>()
            .firstOrNull()
    }
    override fun getStudentsByClassroom(clasroomId: Int): List<Student> {
        return handle.createQuery(
            """
            SELECT name, email, Users.id, github_username, github_id, is_created, school_id,token FROM Student
            JOIN Users on Users.id = Student.id
            JOIN student_course on student_course.student = Student.id
            JOIN classroom on classroom.course_id = student_course.course
            WHERE classroom.id = :classroom_id
            """,
        )
            .bind("classroom_id", clasroomId)
            .mapTo<Student>()
            .list()
    }

    override fun addStudentToClassroom(classroomId: Int, studentId: Int) {
        handle.createUpdate(
            """
            INSERT INTO student_course (student, course)
            VALUES (:student_id, (SELECT course_id FROM classroom WHERE id = :classroom_id))
            """,
        )
            .bind("student_id", studentId)
            .bind("classroom_id", classroomId)
            .execute()
    }

    override fun getAllInviteLinks(): List<String> {
        return handle.createQuery(
            """
            SELECT invite_link FROM Classroom
            """,
        )
            .mapTo<String>()
            .list()
    }
}
