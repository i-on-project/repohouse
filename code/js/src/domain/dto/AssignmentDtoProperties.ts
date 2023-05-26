import { SirenEntity } from "../../http/Siren"
import { AssignmentDomain } from "../Assignment"
import { TeamDomain } from "../Team";
import { DeliveryDomain } from "../Delivery"

export type TeacherAssignmentDto = SirenEntity<TeacherAssignmentDtoProperties>
export type StudentAssignmentDto = SirenEntity<StudentAssignmentDtoProperties>

export class AssignmentDtoProperties {
    assignment: AssignmentDomain;
    deliveries: DeliveryDomain[];
}

export class TeacherAssignmentDtoProperties extends AssignmentDtoProperties{
    constructor(
        assignment: AssignmentDomain,
        deliveries: DeliveryDomain[],
        teams: TeamDomain[],
    ) {
        super()
        this.assignment = assignment
        this.deliveries = deliveries
        this.teams = teams
    }

    assignment: AssignmentDomain;
    deliveries: DeliveryDomain[];
    teams: TeamDomain[];
}

export class StudentAssignmentDtoProperties extends AssignmentDtoProperties{
    constructor(
        assignment: AssignmentDomain,
        deliveries: DeliveryDomain[],
        team: TeamDomain,
    ) {
        super()
        this.assignment = assignment
        this.deliveries = deliveries
        this.team = team
    }

    assignment: AssignmentDomain;
    deliveries: DeliveryDomain[];
    team: TeamDomain;
}

export interface AssignmentDeletedDtoProperties{
    deleted: boolean
}

export class AssignmentDeletedDtoProperties {
    constructor(
        deleted: boolean
    ) {
        this.deleted = deleted
    }
}

export class AssignmentBody{
    constructor(
        classroomId: number,
        minNumberElems: number,
        maxNumberElems: number,
        maxNumberGroups: number,
        title: string,
        description: string,
        dueDate: Date,
    ) {
        this.classroomId = classroomId
        this.minNumberElems = minNumberElems
        this.maxNumberElems = maxNumberElems
        this.maxNumberGroups = maxNumberGroups
        this.title = title
        this.description = description
        this.dueDate = dueDate
    }

    classroomId: number
    minNumberElems: number
    maxNumberElems: number
    maxNumberGroups: number
    description: string
    title: string
    dueDate: Date
}
