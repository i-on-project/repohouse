import {SirenEntity} from "../../siren/Siren";
import {DeliveryDomain} from "../Delivery";
import {TeamDomain} from "../Team";

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