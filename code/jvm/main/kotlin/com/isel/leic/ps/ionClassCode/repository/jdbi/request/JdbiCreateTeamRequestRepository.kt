package com.isel.leic.ps.ionClassCode.repository.jdbi.request

import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ionClassCode.domain.requests.CreateTeam
import com.isel.leic.ps.ionClassCode.repository.request.CreateTeamRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Create Team Request methods
 */
class JdbiCreateTeamRequestRepository(
    private val handle: Handle,
) : CreateTeamRepository {

    /**
     * Method to create a Create Team Request
     */
    override fun createCreateTeamRequest(request: CreateTeamInput, creator: Int): CreateTeam {
        val id = handle.createUpdate(
            """
            INSERT INTO request (creator, composite, state)
            VALUES (:creator, :compositeId, 'Pending')
            RETURNING id
            """,
        )
            .bind("creator", creator)
            .bind("compositeId", request.composite)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

        handle.createUpdate(
            """
            INSERT INTO createteam (id, team_id)
            VALUES (:id, :teamId)
            """,
        )
            .bind("id", id)
            .bind("teamId", request.teamId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

        return CreateTeam(id = id, creator = creator, composite = request.composite, teamId = request.teamId, githubTeamId = null, teamName = request.teamName)
    }

    /**
     * Method to get all Create Team Request's
     */
    override fun getCreateTeamRequests(): List<CreateTeam> {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite, x.team_id, x.name as team_name, x.github_team_id FROM 
            (SELECT c.id, c.team_id, t.name, c.github_team_id FROM createteam c JOIN team t on t.id = c.team_id) as x  
            JOIN request r on r.id = x.id
            """,
        )
            .mapTo<CreateTeam>()
            .list()
    }

    /**
     * Method to get a Create Team Request by is id
     */
    override fun getCreateTeamRequestById(id: Int): CreateTeam? {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite, x.team_id, x.name as team_name, x.github_team_id FROM 
            (SELECT c.id, c.team_id, t.name, c.github_team_id FROM createteam c JOIN team t on t.id = c.team_id) as x 
            JOIN request r on r.id = x.id
            WHERE x.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<CreateTeam>()
            .firstOrNull()
    }

    override fun getCreateTeamRequests(teamIds: List<Team>): List<CreateTeam> {
        return teamIds.map { team ->
            handle.createQuery(
                """
                SELECT r.id, r.creator, r.state, r.composite, x.team_id, x.name as team_name, x.github_team_id FROM (
                    SELECT c.id, t.name, c.team_id, c.github_team_id FROM createteam as c JOIN team t on c.team_id = t.id
                    WHERE c.team_id = :teamId
                ) as x JOIN request r on r.id = x.id
                """,
            )
                .bind("teamId", team.id)
                .mapTo<CreateTeam>()
                .first()
        }
    }

    override fun getCreateTeamRequestByCompositeId(compositeId: Int): CreateTeam? {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite, x.team_id, x.name as team_name, x.github_team_id FROM (
                SELECT c.id, t.name, c.team_id, c.github_team_id FROM createteam as c JOIN team t on c.team_id = t.id
            ) as x JOIN request r on r.id = x.id
            WHERE r.composite = :compositeId
            """,
        )
            .bind("compositeId", compositeId)
            .mapTo<CreateTeam>()
            .firstOrNull()
    }

    override fun getCreateTeamRequestByTeamId(teamId: Int): CreateTeam? {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite, x.team_id, x.name as team_name, x.github_team_id FROM (
                SELECT c.id, t.name, c.team_id, c.github_team_id FROM createteam as c JOIN team t on c.team_id = t.id
                WHERE c.team_id = :teamId
            ) as x JOIN request r on r.id = x.id
            """,
        )
            .bind("teamId", teamId)
            .mapTo<CreateTeam>()
            .firstOrNull()
    }

    override fun updateCreateTeamRequestState(requestId: Int, state: String, githubTeamId: Int?) {
        handle.createUpdate(
            """
            UPDATE request SET state = :state
            WHERE id = :requestId
            """,
        )
            .bind("requestId", requestId)
            .bind("state", state)
            .execute()
        handle.createUpdate(
            """
            UPDATE createteam SET github_team_id = :githubTeamId
            WHERE id = :requestId
            """,
        )
            .bind("requestId", requestId)
            .bind("githubTeamId", githubTeamId)
            .execute()
    }
}
