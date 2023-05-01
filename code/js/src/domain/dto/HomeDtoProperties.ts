import { SirenEntity } from "../../http/Siren"

export type HomeDto = SirenEntity<HomeDtoProperties>

export interface HomeDtoProperties{
    title: string,
    description: string,
    est: string,
}

export class HomeDtoProperties {
    constructor(
        title: string,
        description: string,
        est: string,
    ) {
        this.title = title
        this.description = description
        this.est = est
    }
}
