package com.isel.leic.ps.ion_classcode.repository.transaction

import com.isel.leic.ps.ion_classcode.repository.AssignmentRepository
import com.isel.leic.ps.ion_classcode.repository.ClassroomRepository
import com.isel.leic.ps.ion_classcode.repository.CooldownRepository
import com.isel.leic.ps.ion_classcode.repository.CourseRepository
import com.isel.leic.ps.ion_classcode.repository.DeliveryRepository
import com.isel.leic.ps.ion_classcode.repository.FeedbackRepository
import com.isel.leic.ps.ion_classcode.repository.OutboxRepository
import com.isel.leic.ps.ion_classcode.repository.RepoRepository
import com.isel.leic.ps.ion_classcode.repository.TagRepository
import com.isel.leic.ps.ion_classcode.repository.TeamRepository
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import com.isel.leic.ps.ion_classcode.repository.request.ApplyRequestRepository
import com.isel.leic.ps.ion_classcode.repository.request.CompositeRepository
import com.isel.leic.ps.ion_classcode.repository.request.CreateRepoRepository
import com.isel.leic.ps.ion_classcode.repository.request.CreateTeamRepository
import com.isel.leic.ps.ion_classcode.repository.request.JoinTeamRepository
import com.isel.leic.ps.ion_classcode.repository.request.LeaveCourseRepository
import com.isel.leic.ps.ion_classcode.repository.request.LeaveTeamRepository
import com.isel.leic.ps.ion_classcode.repository.request.RequestRepository

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
    val applyRequestRepository: ApplyRequestRepository
    val archiveRepoRepository: AssignmentRepository
    val compositeRepository: CompositeRepository
    val createRepoRepository: CreateRepoRepository
    val createTeamRepository: CreateTeamRepository
    val joinTeamRepository: JoinTeamRepository
    val leaveTeamRepository: LeaveTeamRepository
    val leaveCourseRepository: LeaveCourseRepository
    val requestRepository: RequestRepository
    val outboxRepository: OutboxRepository
    val cooldownRepository: CooldownRepository
    fun rollback()
}
