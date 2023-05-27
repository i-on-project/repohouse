export interface TeamDomain {
    id: number,
    name: string,
    isCreated: Boolean,
    isClosed: Boolean,
    assignment: number,
}

export class TeamDomain {
    constructor(
        id: number,
        name: string,
        isCreated: Boolean,
        isClosed: Boolean,
        assignment: number,
    ) {
        this.id = id
        this.name = name
        this.isCreated = isCreated
        this.isClosed = isClosed
        this.assignment = assignment
    }
}