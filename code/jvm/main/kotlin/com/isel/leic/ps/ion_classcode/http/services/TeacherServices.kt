package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.http.controllers.AuthController
import com.isel.leic.ps.ion_classcode.http.model.output.CourseOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserInfo
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.transaction.JdbiTransaction
import com.isel.leic.ps.ion_classcode.repository.jdbi.transaction.JdbiTransactionManager
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import java.text.FieldPosition
import org.springframework.stereotype.Component

typealias CoursesResponse = Either<TeacherServicesError, List<CourseOutputModel>>


sealed class TeacherServicesError {

}

@Component
class TeacherServices(
    private val transactionManager: TransactionManager,
) {
   /*
    fun getCourses(teacherId: Int): CoursesResponse {

        return transactionManager.run {
            val courses = it.courseRepository.getCourses()
            Either.Right(courses)
        }
    }
*/
}
