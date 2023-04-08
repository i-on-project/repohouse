import {HomeDto} from "../dto/HomeDtoProperties";

export interface Home{
    title: string,
    description: string,
    est: string,
}

export class Home{
    constructor(
        dto: HomeDto
    ) {
        const home = dto.properties
        if(home == null) throw new Error("HomeDto properties is null")
        this.title = home.title
        this.description = home.description
        this.est = home.est
    }
}