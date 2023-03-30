package com.isel.leic.ps.ion_classcode.http.controllers

import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.TeachersPendingInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuStudentOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuTeacherOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeachersPendingOutputModel
import com.isel.leic.ps.ion_classcode.http.services.StudentServices
import com.isel.leic.ps.ion_classcode.http.services.TeacherServices
import com.isel.leic.ps.ion_classcode.http.services.UserServices
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.SirenModel
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import org.jetbrains.annotations.NotNull
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MenuController(
    private val userServices: UserServices,
    private val teacherServices: TeacherServices,
    private val studentServices: StudentServices
) {

    @GetMapping(Uris.MENU_PATH, headers = ["User=Teacher"])
    fun menuTeacher(
        user:User
    ): SirenModel<MenuOutputModel> {
        return when(val courses = user.id?.let { teacherServices.getCourses(it)}) {
            is Either.Right -> siren(value = MenuTeacherOutputModel(user.name, user.email,courses.value.map { CourseOutputModel(it.id,it.orgUrl,it.name,it.teacherId) } )){
                clazz("menu")
                link(rel = LinkRelation("self"), href = Uris.menuUri(), needAuthentication = true)
                link(rel = LinkRelation("credits"), href = Uris.creditsUri())
                link(rel = LinkRelation("logout"), href = Uris.logoutUri(), needAuthentication = true)
                link(rel = LinkRelation("createCourse"), href = Uris.createCourseUri(), needAuthentication = true)
                link(rel = LinkRelation("teachersApproval"), href = Uris.teachersApprovalUri(), needAuthentication = true)
                courses.value.forEach{
                    link(rel = LinkRelation("course"), href = Uris.courseUri(it.id), needAuthentication = true)
                }
            }
            is Either.Left ->  TODO("ErrorOutputModel")
            else -> TODO("ErrorOutputModel")
        }
    }


    @GetMapping(Uris.MENU_PATH, headers = ["User=Student"])
    fun menuStudent(
        user:User
    ): SirenModel<MenuOutputModel>  {
        val student = user.id?.let { studentServices.getStudentSchoolId(user.id!!) }
        return when(val courses = user.id?.let { studentServices.getCourses(it)}) {
            is Either.Right -> siren(value = MenuStudentOutputModel(user.name, 1,user.email,courses.value.map { CourseOutputModel(it.id,it.orgUrl,it.name,it.teacherId) } )) {
                link(rel = LinkRelation("self"), href = Uris.menuUri(), needAuthentication = true)
                link(rel = LinkRelation("credits"), href = Uris.creditsUri())
                link(rel = LinkRelation("logout"), href = Uris.logoutUri(), needAuthentication = true)
                courses.value.forEach {
                    link(rel = LinkRelation("course"), href = Uris.courseUri(it.id), needAuthentication = true)
                }
            }
            is Either.Left ->  TODO("ErrorOutputModel")
            else -> TODO("ErrorOutputModel")
        }
    }

    @GetMapping(Uris.TEACHERS_APPROVAL_PATH)
    fun teachersApproval(
        user:User
    ):SirenModel<TeachersPendingOutputModel>{

        return when(val teachers = teacherServices.getTeatchersNeedingApproval()) {
            is Either.Right -> siren(value = TeachersPendingOutputModel(teachers.value)) {
                link(rel = LinkRelation("self"), href = Uris.teachersApprovalUri(), needAuthentication = true)
                link(rel = LinkRelation("menu"), href = Uris.menuUri(), needAuthentication = true)
                link(rel = LinkRelation("credits"), href = Uris.creditsUri())
                link(rel = LinkRelation("logout"), href = Uris.logoutUri(), needAuthentication = true)
                teachers.value.forEach {
                    action(name = "approveTeacher",
                        href = Uris.teachersApprovalUri(),
                        method = HttpMethod.POST,
                        type = "application/x-www-form-urlencoded",
                        block = {
                            hiddenField(name = "teacherId", value = it.id.toString())
                        }
                    )
                }
            }

            is Either.Left -> TODO("ErrorOutputModel")

        }
    }

    @PostMapping(Uris.TEACHERS_APPROVAL_PATH)
    fun teacherApproved(
        input: TeachersPendingInputModel
    ):ResponseEntity<Any>{
        return when(teacherServices.approveTeachers(input)){
            is Either.Right -> ResponseEntity
                .status(Status.REDIRECT)
                .header("Location", Uris.TEACHERS_APPROVAL_PATH)
                .build()
            is Either.Left -> ResponseEntity
                .status(Status.BAD_REQUEST)
                .build()
        }
    }
}