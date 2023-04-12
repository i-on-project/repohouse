package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ion_classcode.repository.ClassroomRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Classroom methods
 */
class JdbiClassroomRepository(private val handle: Handle) : ClassroomRepository {
    /**
     * Method to create a Classroom
     */
    override fun createClassroom(classroom: ClassroomInput, inviteLink: String): Int =
        handle.createUpdate(
            """
            INSERT INTO Classroom (name, last_sync, invite_link, is_archived, course_id, teacher_id)
            VALUES (:name, CURRENT_DATE, :invite_link, false, :course_id, :teacher_id)
            RETURNING id
            """,
        )
            .bind("name", classroom.name)
            .bind("invite_link", inviteLink)
            .bind("course_id", classroom.courseId)
            .bind("teacher_id", classroom.teacherId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

    /**
     * Method to update a Classroom name
     */
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

    /**
     * Method to delete a Classroom
     */
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

    /**
     * Method to get a Classroom by is id
     */
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

    /**
     * Method to get all Assigments of a Classroom
     */
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

    /**
     * Method to achive a Classroom
     */
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

    /**
     * Method to get a Classroom by a course and a student id
     */
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

    /**
     * Method to get a Classroom invite link
     */
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

    /**
     * Method to get a Classroom by is invite link
     */
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

    /**
     * Method to get all Classroom students
     */
    override fun getStudentsByClassroom(classroomId: Int): List<Student> {
        return handle.createQuery(
            """
            SELECT distinct u.name, email, u.id, github_username, github_id, u.is_created, token from classroom
            JOIN assignment on classroom.id = assignment.classroom_id
            JOIN team on team.assignment = assignment.id JOIN student_team on student_team.team = team.id
            JOIN users as u on u.id = student_team.student
            WHERE classroom.id = :classroom_id
            """,
        )
            .bind("classroom_id", classroomId)
            .mapTo<Student>()
            .list()
    }

    /**
     * Method to add a student to a Classroom
     */
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

    /**
     * Method to get all Classroom's invite links
     */
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
