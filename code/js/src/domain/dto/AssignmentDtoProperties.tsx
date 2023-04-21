import {SirenEntity} from "../../siren/Siren";
import {AssignmentDomain} from "../Assignment";
import {TeamDomain} from "../Team";
import {DeliveryDomain} from "../Delivery";

export type AssignmentDto = SirenEntity<AssignmentDtoProperties>

export interface AssignmentDtoProperties {
    assigment: AssignmentDomain,
    deliveries: DeliveryDomain[],
    teams: TeamDomain[],
}

export class AssignmentDtoProperties {
    constructor(
        assigment: AssignmentDomain,
        deliveries: DeliveryDomain[],
        teams: TeamDomain[],
    ) {
        this.assigment = assigment
        this.deliveries = deliveries
        this.teams = teams
    }
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
        maxNumberElems: number,
        maxNumberGroups: number,
        description: string,
        title: string,
        dueDate: Date,
    ) {
        this.classroomId = classroomId
        this.maxNumberElems = maxNumberElems
        this.maxNumberGroups = maxNumberGroups
        this.description = description
        this.title = title
        this.dueDate = dueDate
    }

    classroomId: number
    maxNumberElems: number
    maxNumberGroups: number
    description: string
    title: string
    dueDate: Date
}