export interface DeliveryDomain {
    id: number,
    dueDate: Date,
    tagControl: string,
    assignmentId: number,
    lastSync: Date,
}

export class DeliveryDomain {
    constructor(
        id: number,
        dueDate: Date,
        tagControl: string,
        assignmentId: number,
        lastSync: Date,
    ) {
        this.id = id
        this.dueDate = dueDate
        this.tagControl = tagControl
        this.assignmentId = assignmentId
        this.lastSync = lastSync
    }
}