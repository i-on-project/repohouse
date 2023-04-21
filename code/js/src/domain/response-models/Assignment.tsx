import {AssignmentDomain} from "../Assignment";
import {DeliveryDomain} from "../Delivery";
import {TeamDomain} from "../Team";
import {AssignmentDto} from "../dto/AssignmentDtoProperties";


export interface Assignment {
    assignment: AssignmentDomain,
    deliveries: DeliveryDomain[],
    teams: TeamDomain[],
}

export class Assignment {
    constructor(
        dto: AssignmentDto
    ) {
        const assignment = dto.properties
        if(assignment == null) throw new Error("AssigmentDto properties is null")
        this.assignment = assignment.assigment
        this.deliveries = assignment.deliveries
        this.teams = assignment.teams
    }
}