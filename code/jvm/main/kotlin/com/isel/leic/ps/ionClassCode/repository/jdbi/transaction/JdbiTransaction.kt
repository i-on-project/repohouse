package com.isel.leic.ps.ionClassCode.repository.jdbi.transaction

import com.isel.leic.ps.ionClassCode.repository.ApplyRepository
import com.isel.leic.ps.ionClassCode.repository.AssignmentRepository
import com.isel.leic.ps.ionClassCode.repository.ClassroomRepository
import com.isel.leic.ps.ionClassCode.repository.CooldownRepository
import com.isel.leic.ps.ionClassCode.repository.CourseRepository
import com.isel.leic.ps.ionClassCode.repository.DeliveryRepository
import com.isel.leic.ps.ionClassCode.repository.FeedbackRepository
import com.isel.leic.ps.ionClassCode.repository.OtpRepository
import com.isel.leic.ps.ionClassCode.repository.OutboxRepository
import com.isel.leic.ps.ionClassCode.repository.RepoRepository
import com.isel.leic.ps.ionClassCode.repository.TagRepository
import com.isel.leic.ps.ionClassCode.repository.TeamRepository
import com.isel.leic.ps.ionClassCode.repository.UsersRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiApplyRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiAssignmentRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiClassroomRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiCooldownRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiCourseRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiDeliveryRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiFeedbackRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiOtpRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiOutboxRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiRepoRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiTagRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiTeamRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiArchiveRepoRequestRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiCompositeRequestRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiCreateRepoRequestRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiCreateTeamRequestRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiJoinTeamRequestRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiLeaveCourseRequestRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiLeaveTeamRequestRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiRequestRepository
import com.isel.leic.ps.ionClassCode.repository.request.ArchiveRepoRequestRepository
import com.isel.leic.ps.ionClassCode.repository.request.CompositeRepository
import com.isel.leic.ps.ionClassCode.repository.request.CreateRepoRepository
import com.isel.leic.ps.ionClassCode.repository.request.CreateTeamRepository
import com.isel.leic.ps.ionClassCode.repository.request.JoinTeamRepository
import com.isel.leic.ps.ionClassCode.repository.request.LeaveCourseRepository
import com.isel.leic.ps.ionClassCode.repository.request.LeaveTeamRepository
import com.isel.leic.ps.ionClassCode.repository.request.RequestRepository
import com.isel.leic.ps.ionClassCode.repository.transaction.Transaction
import org.jdbi.v3.core.Handle

/**
 * Class of a transaction with lazy implementation of each class repository
 */
class JdbiTransaction(
    private val handle: Handle
) : Transaction {
    override val assignmentRepository: AssignmentRepository by lazy { JdbiAssignmentRepository(handle = handle) }
    override val classroomRepository: ClassroomRepository by lazy { JdbiClassroomRepository(handle = handle) }
    override val feedbackRepository: FeedbackRepository by lazy { JdbiFeedbackRepository(handle = handle) }
    override val repoRepository: RepoRepository by lazy { JdbiRepoRepository(handle = handle) }
    override val teamRepository: TeamRepository by lazy { JdbiTeamRepository(handle = handle) }
    override val usersRepository: UsersRepository by lazy { JdbiUsersRepository(handle = handle) }
    override val tagRepository: TagRepository by lazy { JdbiTagRepository(handle = handle) }
    override val courseRepository: CourseRepository by lazy { JdbiCourseRepository(handle = handle) }
    override val deliveryRepository: DeliveryRepository by lazy { JdbiDeliveryRepository(handle = handle) }
    override val applyRequestRepository: ApplyRepository by lazy { JdbiApplyRepository(handle = handle) }
    override val archiveRepoRepository: ArchiveRepoRequestRepository by lazy { JdbiArchiveRepoRequestRepository(handle = handle) }
    override val compositeRepository: CompositeRepository by lazy { JdbiCompositeRequestRepository(handle = handle) }
    override val createRepoRepository: CreateRepoRepository by lazy { JdbiCreateRepoRequestRepository(handle = handle) }
    override val createTeamRepository: CreateTeamRepository by lazy { JdbiCreateTeamRequestRepository(handle = handle) }
    override val joinTeamRepository: JoinTeamRepository by lazy { JdbiJoinTeamRequestRepository(handle = handle) }
    override val leaveTeamRepository: LeaveTeamRepository by lazy { JdbiLeaveTeamRequestRepository(handle = handle) }
    override val leaveCourseRepository: LeaveCourseRepository by lazy { JdbiLeaveCourseRequestRepository(handle = handle) }
    override val requestRepository: RequestRepository by lazy { JdbiRequestRepository(handle = handle) }
    override val outboxRepository: OutboxRepository by lazy { JdbiOutboxRepository(handle = handle) }
    override val cooldownRepository: CooldownRepository by lazy { JdbiCooldownRepository(handle = handle) }
    override val otpRepository: OtpRepository by lazy { JdbiOtpRepository(handle = handle) }
    override fun rollback() {
        handle.rollback()
    }
}
