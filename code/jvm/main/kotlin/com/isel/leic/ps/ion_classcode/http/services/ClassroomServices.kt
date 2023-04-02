package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias ClassroomResponse = Either<ClassroomServicesError, Int>

sealed class ClassroomServicesError {
    object ClasroomNotFound : ClassroomServicesError()
}


@Component
class ClassroomServices(
    private val transactionManager: TransactionManager,
) {

    fun getStudentClassroom(classroomId: Int,studentId:Int): ClassroomResponse {
        return transactionManager.run {
            when (val classrooms = it.classroomRepository.getStudentClassroomId(classroomId,studentId)) {
                null -> Either.Left(ClassroomServicesError.ClasroomNotFound)
                else -> Either.Right(classrooms)
            }
        }
    }

}