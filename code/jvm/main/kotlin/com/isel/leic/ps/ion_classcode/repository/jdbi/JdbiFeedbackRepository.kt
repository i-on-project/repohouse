package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Feedback
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.repository.FeedbackRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Feedback methods
 */
class JdbiFeedbackRepository(private val handle: Handle) : FeedbackRepository {

    /**
     * Method to create a Feedback
     */
    override fun createFeedback(feedback: FeedbackInput): Feedback {
        val id = handle.createUpdate(
            """
                INSERT INTO FEEDBACK (description,label,team_id) 
                VALUES (:description, :label, :teamId)
                RETURNING id
                """,
        )
            .bind("description", feedback.description)
            .bind("label", feedback.label)
            .bind("teamId", feedback.teamId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        return Feedback(id ,feedback.description, feedback.label, feedback.teamId)
    }

    /**
     * Method to delete a Feedback
     */
    override fun deleteFeedback(feedbackId: Int) {
        handle.createUpdate(
            """
                DELETE FROM FEEDBACK
                WHERE id = :feedbackId
                """,
        )
            .bind("feedbackId", feedbackId)
            .execute()
    }

    /**
     * Method to update a Feedback description
     */
    override fun updateFeedbackDescription(feedbackId: Int, description: String) {
        handle.createUpdate(
            """
                UPDATE FEEDBACK
                SET description = :description
                WHERE id = :feedbackId
                """,
        )
            .bind("feedbackId", feedbackId)
            .bind("description", description)
            .execute()
    }


    /**
     * Method to update a Feedback label
     */
    override fun updateFeedbackLabel(feedbackId: Int, label: String) {
        handle.createUpdate(
            """
                UPDATE FEEDBACK
                SET label = :label
                WHERE id = :feedbackId
                """,
        )
            .bind("feedbackId", feedbackId)
            .bind("label", label)
            .execute()
    }


    /**
     * Method to get a Feedback by is id
     */
    override fun getFeedbackById(feedbackId: Int): Feedback? {
        return handle.createQuery(
            """
                SELECT * FROM FEEDBACK
                WHERE id = :feedbackId
                """,
        )
            .bind("feedbackId", feedbackId)
            .mapTo<Feedback>()
            .firstOrNull()
    }


    /**
     * Method to get a Feedback by is team
     */
    override fun getFeedbacksByTeam(teamId: Int): List<Feedback> {
        return handle.createQuery(
            """
                SELECT * FROM FEEDBACK
                WHERE team_id = :teamId
                """,
        )
            .bind("teamId", teamId)
            .mapTo<Feedback>()
            .list()
    }
}
