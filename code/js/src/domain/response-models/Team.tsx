import {TeamDto} from "../dto/TeamDtoProperties";
import {TeamDomain} from "../Team";
import {Student} from "../User";
import {RepoDomain} from "../Repo";
import {FeedbackDomain} from "../Feedback";


export interface Team{
    team: TeamDomain,
    students: Student[],
    repos: RepoDomain[],
    feedbacks: FeedbackDomain[],
}

export class Team {
    constructor(
        dto: TeamDto
    ) {
        const team = dto.properties
        if (team == null) throw new Error("TeamDto properties is null")
        this.team = team.team
        this.students = team.students
        this.repos = team.repos
        this.feedbacks = team.feedbacks
    }
}