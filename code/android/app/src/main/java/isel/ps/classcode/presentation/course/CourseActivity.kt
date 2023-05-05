package isel.ps.classcode.presentation.course

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.domain.Course
import isel.ps.classcode.domain.dto.LocalCourseDto
import isel.ps.classcode.presentation.classroom.ClassroomActivity
import isel.ps.classcode.presentation.course.services.CourseServices
import isel.ps.classcode.ui.theme.ClasscodeTheme

class CourseActivity: ComponentActivity() {
    private val courseServices: CourseServices by lazy { (application as DependenciesContainer).courseServices }

    companion object {
        const val COURSE_EXTRA = "COURSE_EXTRA"
        fun navigate(origin: Activity, course: LocalCourseDto) {
            with(origin) {
                val intent = Intent(this, CourseActivity::class.java)
                intent.putExtra(COURSE_EXTRA, course)
                startActivity(intent)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val vm by viewModels<CourseViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CourseViewModel(courseServices = courseServices) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val course = getCourseExtra()
        if (course != null) {
            vm.getClassrooms(courseId = course.id)
        }
        setContent {
            ClasscodeTheme {
                if (course != null) {
                    CourseScreen(
                        course = course,
                        onBackRequest = { finish() },
                        onClassroomSelected = { classroom ->
                            ClassroomActivity.navigate(origin = this, classroom = classroom.toLocalClassroomDto())
                        },
                        classrooms = vm.classrooms,
                    )
                }
                else {
                    // TODO(): Show a error alert dialog saying that some went wrong
                    finish()
                }
            }
        }
    }

    @Suppress("deprecation")
    private fun getCourseExtra(): Course? {
        val courseExtra: LocalCourseDto? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(COURSE_EXTRA, LocalCourseDto::class.java)
            else
                intent.getParcelableExtra(COURSE_EXTRA)
        return courseExtra?.toCourseDto()
    }

}