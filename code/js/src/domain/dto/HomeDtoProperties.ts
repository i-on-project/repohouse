import { SirenEntity } from "../../http/Siren"

export type HomeDto = SirenEntity<HomeDtoProperties>

export interface HomeDtoProperties{
    title: string,
    description: string,
    subDescription: string,
    est: string,
}

export class HomeDtoProperties {
    constructor(
        title: string,
        description: string,
        subDescription: string,
        est: string,
    ) {
        this.title = title
        this.description = description
        this.subDescription = subDescription
        this.est = est
    }
}
