import {DeliveryDto} from "../dto/DeliveryDtoProperties";
import {TeamDomain} from "../Team";
import {DeliveryDomain} from "../Delivery";

export interface Delivery {
    delivery: DeliveryDomain,
    teamsDelivered: TeamDomain[],
    teamsNotDelivered: TeamDomain[],
}

export class Delivery {
    constructor(
        dto: DeliveryDto
    ) {
        const delivery = dto.properties
        if(delivery == null) throw new Error("DeliveryDto properties is null")
        this.delivery = delivery.delivery
        this.teamsDelivered = delivery.teamsDelivered
        this.teamsNotDelivered = delivery.teamsNotDelivered
    }
}