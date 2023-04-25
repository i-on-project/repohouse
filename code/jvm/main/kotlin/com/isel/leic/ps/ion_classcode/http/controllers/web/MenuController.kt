package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.TeachersPendingInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuStudentOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuTeacherOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeachersPendingOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.StudentServices
import com.isel.leic.ps.ion_classcode.http.services.StudentServicesError
import com.isel.leic.ps.ion_classcode.http.services.TeacherServices
import com.isel.leic.ps.ion_classcode.http.services.TeacherServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Menu Controller
 */
@RestController
class MenuController(
    private val teacherServices: TeacherServices,
    private val studentServices: StudentServices,
) {

    /**
     * Get all courses belonging to the user
     */
    @GetMapping(Uris.MENU_PATH, produces = ["application/vnd.siren+json"])
    fun menu(
        user: User,
    ): ResponseEntity<*> {
        return if (user is Student) {
            menuStudent(user)
        } else {
            menuTeacher(user)
        }
    }

    /**
     * Menu for teachers
     * Link to approve teachers
     */
    private fun menuTeacher(
        user: User,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val courses = teacherServices.getCourses(user.id)) {
            is Either.Right -> siren(value = MenuTeacherOutputModel(user.name, user.email, courses.value.map { CourseOutputModel(it.id, it.orgUrl, it.name, it.teachers) })) {
                clazz("menu")
            }
            is Either.Left -> problemTeacher(courses.value)
        }
    }

    /**
     * Menu for students
     */
    private fun menuStudent(
        user: User,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.notStudent
        val studentSchoolId = studentServices.getStudentSchoolId(user.id)
        if (studentSchoolId is Either.Left) return problemStudent(studentSchoolId.value)
        return when (val courses = studentServices.getCourses(user.id)) {
            is Either.Right -> siren(value = MenuStudentOutputModel(user.name, (studentSchoolId as Either.Right).value, user.email, courses.value.map { CourseOutputModel(it.id, it.orgUrl, it.name, it.teachers) })) {
                link(rel = LinkRelation("self"), href = Uris.menuUri(), needAuthentication = true)
            }
            is Either.Left -> problemStudent(courses.value)
        }
    }

    /**
     * Get all teachers needing approval
     */
    @GetMapping(Uris.TEACHERS_APPROVAL_PATH, produces = ["application/vnd.siren+json"])
    fun teachersApproval(
        user: User,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.stateMismatch
        return when (val teachers = teacherServices.getTeachersNeedingApproval()) {
            is Either.Right -> siren(value = TeachersPendingOutputModel(teachers.value)) {
                clazz("teachersApproval")
                link(rel = LinkRelation("self"), href = Uris.teachersApprovalUri(), needAuthentication = true)
            }

            is Either.Left -> problemTeacher(teachers.value)
        }
    }

    /**
     * Approve teacher
     */
    @PostMapping(Uris.TEACHERS_APPROVAL_PATH, produces = ["application/vnd.siren+json"])
    fun teacherApproved(
        @RequestBody
        input: TeachersPendingInputModel,
    ): ResponseEntity<*> {
        return when (val approved = teacherServices.approveTeachers(input)) {
            is Either.Right -> siren(value = TeachersPendingOutputModel(approved.value)) {}
            is Either.Left -> problemTeacher(approved.value)
        }
    }

    /**
     * Function to handle errors from student
     */
    private fun problemStudent(error: StudentServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            is StudentServicesError.UserNotFound -> Problem.userNotFound
            is StudentServicesError.CourseNotFound -> Problem.courseNotFound
            is StudentServicesError.InvalidInput -> Problem.invalidInput
        }
    }

    /**
     * Function to handle errors from teacher
     */
    private fun problemTeacher(error: TeacherServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            is TeacherServicesError.CourseNotFound -> Problem.courseNotFound
            is TeacherServicesError.TeacherNotFound -> Problem.userNotFound
            is TeacherServicesError.InvalidData -> Problem.invalidInput
        }
    }
}
