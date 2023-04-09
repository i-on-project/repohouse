import {FeedbackDto} from "../dto/FeedbackDtoProperties";

export interface Feedback {
    id: number,
    created:Boolean
}

export class Feedback {
    constructor(
        dto: FeedbackDto
    ) {
        const feedback = dto.properties
        if(feedback == null) throw new Error("FeedbackDto properties is null")
        this.id = feedback.id
        this.created = feedback.created
    }
}