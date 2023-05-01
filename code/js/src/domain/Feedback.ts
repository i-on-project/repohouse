
export interface FeedbackDomain {
    id: number,
    description: string,
    label: string,
    teamId: number,
}

export class FeedbackDomain {
    constructor(
        id: number,
        description: string,
        label: string,
        teamId: number,
    ) {
        this.id = id
        this.description = description
        this.label = label
        this.teamId = teamId
    }
}