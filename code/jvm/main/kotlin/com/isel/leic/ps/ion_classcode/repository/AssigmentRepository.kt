package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.AssigmentInput
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput

interface AssigmentRepository {
    fun createAssigment(assigment: AssigmentInput)
    fun deleteAssigment(assigmentId: Int)
    fun createFeedback(courseId: Int, classroomId: Int, teamId: Int, feedback: FeedbackInput)
    fun deleteFeedback(courseId: Int, classroomId: Int, teamId: Int, feedbackId: Int)
}
