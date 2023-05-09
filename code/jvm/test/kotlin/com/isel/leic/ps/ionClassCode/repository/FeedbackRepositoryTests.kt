package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.input.FeedbackInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiFeedbackRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class FeedbackRepositoryTests {

    @Test
    fun `can create a feedback`() = testWithHandleAndRollback { handle ->
        val feedbackRepo = JdbiFeedbackRepository(handle = handle)
        val teamId = 1
        val created = feedbackRepo.createFeedback(feedback = FeedbackInput(description = "description", label = "label", teamId = teamId))
        val feedback = feedbackRepo.getFeedbackById(feedbackId = created.id)
        assert(feedback != null)
    }

    @Test
    fun `can get a feedback by id`() = testWithHandleAndRollback { handle ->
        val feedbackRepo = JdbiFeedbackRepository(handle = handle)
        val feedbackId = 1
        val label = "label1"
        val feedback = feedbackRepo.getFeedbackById(feedbackId = feedbackId) ?: fail("Feedback not found")
        assert(feedback.label == label)
    }

    @Test
    fun `can update feedback description`() = testWithHandleAndRollback { handle ->
        val feedbackRepo = JdbiFeedbackRepository(handle = handle)
        val feedbackId = 1
        val newDescription = "new description"
        feedbackRepo.updateFeedbackDescription(feedbackId = feedbackId, description = newDescription)
        val feedback = feedbackRepo.getFeedbackById(feedbackId = feedbackId) ?: fail("Feedback not found")
        assert(feedback.description == newDescription)
    }

    @Test
    fun `can update feedback label`() = testWithHandleAndRollback { handle ->
        val feedbackRepo = JdbiFeedbackRepository(handle = handle)
        val feedbackId = 1
        val newLabel = "new label"
        feedbackRepo.updateFeedbackLabel(feedbackId = feedbackId, label = newLabel)
        val feedback = feedbackRepo.getFeedbackById(feedbackId = feedbackId) ?: fail("Feedback not found")
        assert(feedback.label == newLabel)
    }

    @Test
    fun `can get feedbacks by teamId`() = testWithHandleAndRollback { handle ->
        val feedbackRepo = JdbiFeedbackRepository(handle = handle)
        val teamId = 1
        val list = feedbackRepo.getFeedbacksByTeam(teamId = teamId)
        assert(list.size == 2)
    }

    @Test
    fun `can delete a feedback`() = testWithHandleAndRollback { handle ->
        val feedbackRepo = JdbiFeedbackRepository(handle = handle)
        val feedbackId = 3
        feedbackRepo.deleteFeedback(feedbackId = feedbackId)
        val feedback = feedbackRepo.getFeedbackById(feedbackId = feedbackId)
        assert(feedback == null)
    }
}
