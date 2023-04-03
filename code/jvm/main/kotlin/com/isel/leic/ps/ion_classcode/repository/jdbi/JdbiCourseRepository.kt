package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.repository.CourseRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiCourseRepository(private val handle: Handle) : CourseRepository {
    override fun createCourse(course: CourseInput): Course {
        val id = handle.createUpdate(
            """
            INSERT INTO Course (name, org_url, teacher_id)
            VALUES (:name,:org_url,:teacher_id)
            RETURNING id
            """,
        )
            .bind("name", course.name)
            .bind("org_url", course.orgUrl)
            .bind("teacher_id", course.teacherId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        return Course(id, course.orgUrl, course.name, course.teacherId)
    }

    override fun deleteCourse(courseId: Int) {
        handle.createUpdate(
            """
            DELETE FROM Course
            WHERE id = :id
            """,
        )
            .bind("id", courseId)
            .execute()
    }

    override fun enterCourse(courseId: Int, studentId: Int): Course {
        handle.createUpdate(
            """
            INSERT INTO student_course (student,course)
            VALUES (:student_id,:course_id)
            """,
        )
            .bind("student_id", studentId)
            .bind("course_id", courseId)
            .execute()

        return handle.createQuery(
            """
                SELECT * FROM Course
                WHERE id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .mapTo<Course>()
            .first()
    }

    override fun leaveCourse(courseId: Int, studentId: Int): Course {
        handle.createUpdate(
            """
            DELETE FROM student_course
            WHERE student = :student_id AND course = :course_id
            """,
        )
            .bind("student_id", studentId)
            .bind("course_id", courseId)
            .execute()

        return handle.createQuery(
            """
                SELECT * FROM Course
                WHERE id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .mapTo<Course>()
            .first()
    }

    override fun archiveCourse(courseId: Int) {
        handle.createUpdate(
            """
            UPDATE Course
            SET is_archived = true
            WHERE id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .execute()

        handle.createUpdate(
            """
            UPDATE classroom
            SET is_archived = true
            WHERE course_id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .execute()
    }

    override fun getCourseClassrooms(courseId: Int): List<Classroom> {
        return handle.createQuery(
            """
                SELECT * FROM classroom
                WHERE course_id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .mapTo<Classroom>()
            .list()
    }

    override fun getAllTeacherCourses(teacherId: Int): List<Course> {
        return handle.createQuery(
            """
            SELECT * FROM Course
            WHERE teacher_id = :teacher_id
            """,
        )
            .bind("teacher_id", teacherId)
            .mapTo<Course>()
            .list()
    }

    override fun getAllStudentCourses(studentId: Int): List<Course> {
        return handle.createQuery(
            """
            SELECT id,org_url,name,teacher_id FROM Course JOIN student_course ON Course.id = student_course.course
            WHERE student = :student_id
            """,
        )
            .bind("student_id", studentId)
            .mapTo<Course>()
            .list()
    }

    override fun getStudentInCourse(courseId: Int): List<Student> {
        return handle.createQuery(
            """
            SELECT name, email, s.id, github_username, github_id, is_created, users.token,s.school_id FROM student_course JOIN student as s ON student_course.student = s.id JOIN users on s.id = users.id
            WHERE course = :course_id
            """,
        )
            .bind("course_id", courseId)
            .mapTo<Student>()
            .list()
    }

    override fun getCourse(courseId: Int): Course? {
        return handle.createQuery(
            """
            SELECT * FROM course 
            WHERE id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .mapTo<Course>()
            .firstOrNull()
    }

    override fun getCourseByOrg(orgUrl: String): Course? {
        return handle.createQuery(
            """
            SELECT * FROM course 
            WHERE org_url = :orgUrl
            """,
        )
            .bind("orgUrl", orgUrl)
            .mapTo<Course>()
            .firstOrNull()
    }

    override fun getCourseByName(name: String): Course? {
        return handle.createQuery(
            """
            SELECT * FROM course 
            WHERE name = :name
            """,
        )
            .bind("name", name)
            .mapTo<Course>()
            .firstOrNull()
    }

    override fun isUserInCourse(userId: Int, courseId: Int): Boolean {
        val student = handle.createQuery(
            """
            SELECT student FROM student_course 
            WHERE student = :studentId AND course = :courseId
            """,
        )
            .bind("student", userId)
            .bind("course", courseId)
            .mapTo<Int>()
            .firstOrNull()
        return student != null
    }
}
