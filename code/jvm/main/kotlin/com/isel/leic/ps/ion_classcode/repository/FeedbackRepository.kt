package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Feedback
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput

/**
 * Repository functions for Feedback Repository
 */
interface FeedbackRepository {
    fun createFeedback(feedback: FeedbackInput): Feedback
    fun deleteFeedback(feedbackId: Int)
    fun updateFeedbackDescription(feedbackId: Int, description: String)
    fun updateFeedbackLabel(feedbackId: Int, label: String)
    fun getFeedbackById(feedbackId: Int): Feedback?
    fun getFeedbacksByTeam(teamId: Int): List<Feedback>
}
