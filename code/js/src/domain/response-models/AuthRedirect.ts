import {AuthRedirectDto} from "../dto/AuthRedirectDtoProperties";

export interface AuthRedirect{
    message: string,
    url: string,
}

export class AuthRedirect {
    constructor(
        dto: AuthRedirectDto
    ) {
        const authRedirect = dto.properties
        if(authRedirect == null) throw new Error("AuthRedirectDto properties is null")
        this.message = authRedirect.message
        this.url = authRedirect.url
    }
}