package com.isel.leic.ps.ionClassCode.repository.transaction

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
import com.isel.leic.ps.ionClassCode.repository.request.ArchiveRepoRequestRepository
import com.isel.leic.ps.ionClassCode.repository.request.CompositeRepository
import com.isel.leic.ps.ionClassCode.repository.request.CreateRepoRepository
import com.isel.leic.ps.ionClassCode.repository.request.CreateTeamRepository
import com.isel.leic.ps.ionClassCode.repository.request.JoinTeamRepository
import com.isel.leic.ps.ionClassCode.repository.request.LeaveClassroomRepository
import com.isel.leic.ps.ionClassCode.repository.request.LeaveCourseRepository
import com.isel.leic.ps.ionClassCode.repository.request.LeaveTeamRepository
import com.isel.leic.ps.ionClassCode.repository.request.RequestRepository

/**
 * Transction interface holding all repositories
 */
interface Transaction {
    val assignmentRepository: AssignmentRepository
    val classroomRepository: ClassroomRepository
    val feedbackRepository: FeedbackRepository
    val repoRepository: RepoRepository
    val teamRepository: TeamRepository
    val usersRepository: UsersRepository
    val tagRepository: TagRepository
    val courseRepository: CourseRepository
    val deliveryRepository: DeliveryRepository
    val applyRequestRepository: ApplyRepository
    val archiveRepoRepository: ArchiveRepoRequestRepository
    val compositeRepository: CompositeRepository
    val createRepoRepository: CreateRepoRepository
    val createTeamRepository: CreateTeamRepository
    val joinTeamRepository: JoinTeamRepository
    val leaveTeamRepository: LeaveTeamRepository
    val leaveCourseRepository: LeaveCourseRepository
    val leaveClassroomRepository: LeaveClassroomRepository
    val requestRepository: RequestRepository
    val outboxRepository: OutboxRepository
    val otpRepository: OtpRepository
    val cooldownRepository: CooldownRepository
    fun rollback()
}
