import {SirenEntity} from "../../siren/Siren";
import {AssigmentDomain} from "../Assigment";
import {TeamDomain} from "../Team";
import {DeliveryDomain} from "../Delivery";

export type AssigmentDto = SirenEntity<AssigmentDtoProperties>

export interface AssigmentDtoProperties{
    assigment: AssigmentDomain,
    deliveries: DeliveryDomain[],
    teams: TeamDomain[],
}

export class AssigmentDtoProperties {
    constructor(
        assigment: AssigmentDomain,
        deliveries: DeliveryDomain[],
        teams: TeamDomain[],
    ) {
        this.assigment = assigment
        this.deliveries = deliveries
        this.teams = teams
    }
}