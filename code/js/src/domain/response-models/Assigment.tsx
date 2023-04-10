import {AssigmentDomain} from "../Assigment";
import {DeliveryDomain} from "../Delivery";
import {TeamDomain} from "../Team";
import {AssigmentDto} from "../dto/AssigmentDtoProperties";


export interface Assigment{
    assigment: AssigmentDomain,
    deliveries: DeliveryDomain[],
    teams: TeamDomain[],
}

export class Assigment {
    constructor(
        dto: AssigmentDto
    ) {
        const assigment = dto.properties
        if(assigment == null) throw new Error("AssigmentDto properties is null")
        this.assigment = assigment.assigment
        this.deliveries = assigment.deliveries
        this.teams = assigment.teams
    }
}