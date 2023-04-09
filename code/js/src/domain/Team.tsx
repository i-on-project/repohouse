export interface TeamDomain {
    id: number,
    name: string,
    isCreated: Boolean,
    assignment: number,
}

export class TeamDomain {
    constructor(
        id: number,
        name: string,
        isCreated: Boolean,
        assignment: number,
    ) {
        this.id = id
        this.name = name
        this.isCreated = isCreated
        this.assignment = assignment
    }
}