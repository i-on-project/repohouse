package com.isel.leic.ps.ion_classcode.repository.jdbi.transaction

import com.isel.leic.ps.ion_classcode.repository.AssigmentRepository
import com.isel.leic.ps.ion_classcode.repository.ClassroomRepository
import com.isel.leic.ps.ion_classcode.repository.CourseRepository
import com.isel.leic.ps.ion_classcode.repository.DeliveryRepository
import com.isel.leic.ps.ion_classcode.repository.FeedbackRepository
import com.isel.leic.ps.ion_classcode.repository.RepoRepository
import com.isel.leic.ps.ion_classcode.repository.TagRepository
import com.isel.leic.ps.ion_classcode.repository.TeamRepository
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiAssignmentRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiClassroomRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiCourseRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiDeliveryRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiFeedbackRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiRepoRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiTagRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiTeamRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.repository.transaction.Transaction
import org.jdbi.v3.core.Handle


class JdbiTransaction(
    private val handle: Handle,
) : Transaction {
    override val assigmentRepository: AssigmentRepository by lazy { JdbiAssignmentRepository(handle = handle) }
    override val classroomRepository: ClassroomRepository by lazy { JdbiClassroomRepository(handle = handle) }
    override val feedbackRepository: FeedbackRepository by lazy { JdbiFeedbackRepository(handle = handle) }
    override val repoRepository: RepoRepository by lazy { JdbiRepoRepository(handle = handle) }
    override val teamRepository: TeamRepository by lazy { JdbiTeamRepository(handle = handle) }
    override val usersRepository: UsersRepository by lazy { JdbiUsersRepository(handle = handle) }
    override val tagRepository: TagRepository by lazy { JdbiTagRepository(handle = handle) }
    override val courseRepository: CourseRepository by lazy { JdbiCourseRepository(handle = handle) }
    override val deliveryRepository: DeliveryRepository by lazy { JdbiDeliveryRepository(handle = handle) }

    override fun rollback() {
        handle.rollback()
    }
}
