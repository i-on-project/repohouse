import { SirenEntity } from "../../http/Siren"
import { DeliveryDomain } from "../Delivery"
import { TeamDomain } from "../Team"

export type DeliveryDto = SirenEntity<DeliveryDtoProperties>

export interface DeliveryDtoProperties{
    delivery: DeliveryDomain,
    teamsDelivered: TeamDomain[],
    teamsNotDelivered: TeamDomain[],
}

export class DeliveryDtoProperties {
    constructor(
        delivery: DeliveryDomain,
        teamsDelivered: TeamDomain[],
        teamsNotDelivered: TeamDomain[],
    ) {
        this.delivery = delivery
        this.teamsDelivered = teamsDelivered
        this.teamsNotDelivered = teamsNotDelivered
    }
}

export interface DeliveryDeletedDtoProperties{
    deleted: boolean
}

export class DeliveryDeletedDtoProperties {
    constructor(
        deleted: boolean
    ) {
        this.deleted = deleted
    }
}

export interface DeliveryBody{
    assignmentId: number,
    dueDate: string,
    tagControl: string,
}

export class DeliveryBody {
    constructor(
        tagControl: string,
        dueDate: string,
        assignmentId: number,
    ) {
        this.assignmentId = assignmentId
        this.dueDate = dueDate
        this.tagControl = tagControl
    }
}
