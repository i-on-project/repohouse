package com.isel.leic.ps.ion_classcode.repository.transaction

import com.isel.leic.ps.ion_classcode.repository.*
import com.isel.leic.ps.ion_classcode.repository.request.ApplyRequestRepository
import com.isel.leic.ps.ion_classcode.repository.request.CompositeRepository
import com.isel.leic.ps.ion_classcode.repository.request.CreateRepoRepository
import com.isel.leic.ps.ion_classcode.repository.request.CreateTeamRepository
import com.isel.leic.ps.ion_classcode.repository.request.JoinTeamRepository
import com.isel.leic.ps.ion_classcode.repository.request.LeaveCourseRepository
import com.isel.leic.ps.ion_classcode.repository.request.LeaveTeamRepository
import com.isel.leic.ps.ion_classcode.repository.request.RequestRepository

interface Transaction {
    val assigmentRepository: AssigmentRepository
    val classroomRepository: ClassroomRepository
    val feedbackRepository: FeedbackRepository
    val repoRepository: RepoRepository
    val teamRepository: TeamRepository
    val usersRepository: UsersRepository
    val tagRepository: TagRepository
    val courseRepository: CourseRepository
    val deliveryRepository: DeliveryRepository
    val applyRequestRepository: ApplyRequestRepository
    val archieveRepoRepository: AssigmentRepository
    val compositeRepository: CompositeRepository
    val createRepoRepository: CreateRepoRepository
    val createTeamRepository: CreateTeamRepository
    val joinTeamRepository: JoinTeamRepository
    val leaveTeamRepository: LeaveTeamRepository
    val leaveCourseRepository: LeaveCourseRepository
    val requestRepository: RequestRepository
    fun rollback()
}