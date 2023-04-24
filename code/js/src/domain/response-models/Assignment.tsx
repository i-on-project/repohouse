import {AssignmentDomain} from "../Assignment";
import {DeliveryDomain} from "../Delivery";
import {TeamDomain} from "../Team";
import {StudentAssignmentDto, TeacherAssignmentDto} from "../dto/AssignmentDtoProperties";


export interface Assignment {
    assignment: AssignmentDomain,
    deliveries: DeliveryDomain[],
}

export class TeacherAssignment implements Assignment {
    constructor(
        dto: TeacherAssignmentDto
    ) {
        const assignment = dto.properties
        if(assignment == null) throw new Error("AssigmentDto properties is null")
        this.assignment = assignment.assignment
        this.deliveries = assignment.deliveries
        this.teams = assignment.teams
    }

    assignment: AssignmentDomain;
    deliveries: DeliveryDomain[];
    teams: TeamDomain[];
}

export class StudentAssignment implements Assignment {
    constructor(
        dto: StudentAssignmentDto
    ) {
        const assignment = dto.properties
        if(assignment == null) throw new Error("AssigmentDto properties is null")
        this.assignment = assignment.assignment
        this.deliveries = assignment.deliveries
        this.team = assignment.team
    }

    assignment: AssignmentDomain;
    deliveries: DeliveryDomain[];
    team: TeamDomain;
}