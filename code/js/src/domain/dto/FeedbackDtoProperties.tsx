import {SirenEntity} from "../../siren/Siren";

export type FeedbackDto = SirenEntity<FeedbackDtoProperties>

export interface FeedbackDtoProperties{
    id: number,
    created:Boolean
}

export class FeedbackDtoProperties {
    constructor(
        id: number,
        created:Boolean
    ) {
        this.id = id
        this.created = created
    }
}