package com.isel.leic.ps.ion_classcode.repository.transaction

import com.isel.leic.ps.ion_classcode.repository.*

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
    fun rollback()
}