package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.http.services.TeamServices
import org.springframework.web.bind.annotation.RestController

@RestController
class TeamController(
    private val teamService: TeamServices,
) {

    // TODO: getTeamInfo :
    //      with links for requestsHistory
    //      with action for post Feedback (if user is teacher)
    //      if team still in pending state, repo link is not available and team actions are not available
    // TODO: exitTeam if user is not teacher :
    //      createRequest to exit team
    //      if user is not teacher and is the last member of the team, delete the team (mobile verification)
    // TODO: requestsHistory:
    //      with action for pending declined requests (if user is teacher)
    // TODO: join/CreateTeam:
    //     list of closed and open teams and option to create a new team (if possible)
}
