export interface DeliveryDomain {
    id: number,
    dueDate: Date,
    tagControl: string,
    assignmentId: number
}

export class DeliveryDomain {
    constructor(
        id: number,
        dueDate: Date,
        tagControl: string,
        assignmentId: number,
    ) {
        this.id = id
        this.dueDate = dueDate
        this.tagControl = tagControl
        this.assignmentId = assignmentId
    }
}