package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Feedback
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.repository.FeedbackRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiFeedbackRepository(private val handle: Handle) : FeedbackRepository {
    override fun createFeedback(feedback: FeedbackInput): Int {
        return handle.createUpdate(
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
    }

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
