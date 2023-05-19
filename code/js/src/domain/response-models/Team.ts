import {TeamDto, TeamsDto} from "../dto/TeamDtoProperties";
import {TeamDomain} from "../Team";
import {Student} from "../User";
import {RepoDomain} from "../Repo";
import {FeedbackDomain} from "../Feedback";


export interface Team{
    team: TeamDomain,
    students: Student[],
    repo: RepoDomain,
    feedbacks: FeedbackDomain[],
}

export interface Teams{
    teams: Team[],
}
export class Teams {
    constructor(
        dto: TeamsDto
    ) {
        const teams = dto.properties
        if (teams == null) throw new Error("TeamDto properties is null")
        this.teams = teams.teams
    }
}

export class Team {
    constructor(
        dto: TeamDto
    ) {
        const team = dto.properties
        if (team == null) throw new Error("TeamDto properties is null")
        this.team = team.team
        this.students = team.students
        this.repo = team.repo
        this.feedbacks = team.feedbacks
    }
}