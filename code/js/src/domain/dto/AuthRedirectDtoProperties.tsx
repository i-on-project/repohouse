import {SirenEntity} from "../../siren/Siren";
import {AssigmentDomain} from "../Assigment";
import {TeamDomain} from "../Team";
import {DeliveryDomain} from "../Delivery";

export type AuthRedirectDto = SirenEntity<AuthRedirectDtoProperties>

export interface AuthRedirectDtoProperties{
    message: string,
    url: string,
}

export class AuthRedirectDtoProperties {
    constructor(
        message: string,
        url: string
    ) {
        this.message = message;
        this.url = url;
    }
}