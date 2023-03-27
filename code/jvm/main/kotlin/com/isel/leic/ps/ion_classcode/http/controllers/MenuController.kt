package com.isel.leic.ps.ion_classcode.http.controllers

import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.TeacherInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuStudentOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuTeacherOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeacherOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeachersOutputModel
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.SirenModel
import com.isel.leic.ps.ion_classcode.infra.siren
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MenuController {

    @GetMapping(Uris.MENU_PATH, headers = ["User=Teacher"])
    fun menuTeacher(

    ): SirenModel<MenuOutputModel> {
        /** Services functions **/
        //val user = getUser()
        //val courses = getCourses()
        /** Services functions **/

        val courses = listOf<CourseOutputModel>()
        return siren(value = MenuTeacherOutputModel("Teacher Name", "", courses)){
            link(rel = LinkRelation("self"), href = Uris.menuUri(), needAuthentication = true)
            link(rel = LinkRelation("credits"), href = Uris.creditsUri())
            link(rel = LinkRelation("logout"), href = Uris.logoutUri(), needAuthentication = true)
            link(rel = LinkRelation("createCourse"), href = Uris.createCourseUri(), needAuthentication = true)
            link(rel = LinkRelation("teachersApproval"), href = Uris.teachersApprovalUri(), needAuthentication = true)
            courses.forEach{
                link(rel = LinkRelation("course"), href = Uris.courseUri(it.id), needAuthentication = true)
            }
        }
    }


    @GetMapping(Uris.MENU_PATH, headers = ["User=Student"])
    fun menuStudent(): SirenModel<MenuOutputModel>  {
        /** Services functions **/
        //val user = getUser()
        //val courses = getCourses()
        /** Services functions **/
        val courses = listOf<CourseOutputModel>()
        return siren(value = MenuStudentOutputModel("Teacher Name", 1111,"", listOf())){
            link(rel = LinkRelation("self"), href = Uris.menuUri(), needAuthentication = true)
            link(rel = LinkRelation("credits"), href = Uris.creditsUri())
            link(rel = LinkRelation("logout"), href = Uris.logoutUri(), needAuthentication = true)
            courses.forEach{
                link(rel = LinkRelation("course"), href = Uris.courseUri(it.id), needAuthentication = true)
            }
        }
    }

    @GetMapping(Uris.TEACHERS_APPROVAL_PATH)
    fun teachersApproval():SirenModel<TeachersOutputModel>{
        /** Services functions **/
        //val teachers = getTeachersNeedingApproval()
        /** Services functions **/
        val teachers = listOf<TeacherOutputModel>()
        return siren(value = TeachersOutputModel(teachers)){
            link(rel = LinkRelation("self"), href = Uris.teachersApprovalUri(), needAuthentication = true)
            link(rel = LinkRelation("menu"), href = Uris.menuUri(), needAuthentication = true)
            link(rel = LinkRelation("credits"), href = Uris.creditsUri())
            link(rel = LinkRelation("logout"), href = Uris.logoutUri(), needAuthentication = true)
            teachers.forEach{
                action(name = "approveTeacher", href = Uris.teachersApprovalUri(), method = HttpMethod.POST,type= "application/x-www-form-urlencoded",
                    block = {
                        hiddenField(name = "teacherId", value = it.id.toString())
                    }
                )
            }
        }
    }

    @PostMapping(Uris.TEACHERS_APPROVAL_PATH)
    fun teacherApproved(
        input: TeacherInputModel
    ):ResponseEntity<Any>{
        /** Services functions **/
        //approveTeachers(input.approved)
        //rejectTeachers(input.rejected)
        /** Services functions **/

        return ResponseEntity
            .status(Status.REDIRECT)
            .header("Location", Uris.TEACHERS_APPROVAL_PATH)
            .build()
    }
}