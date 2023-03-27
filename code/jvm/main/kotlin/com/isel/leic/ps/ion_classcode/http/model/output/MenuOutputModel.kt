package com.isel.leic.ps.ion_classcode.http.model.output

interface MenuOutputModel{
    val name: String
    val email: String
    val courses: List<CourseOutputModel>
}
data class MenuStudentOutputModel(
    override val name: String,
    val schoolNumber: Int,
    override val email: String,
    override val courses: List<CourseOutputModel>
): MenuOutputModel

data class MenuTeacherOutputModel(
    override val name: String,
    override val email: String,
    override val courses: List<CourseOutputModel>
): MenuOutputModel
