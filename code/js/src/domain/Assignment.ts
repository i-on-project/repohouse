export interface AssignmentDomain {
    id: number,
    classroomId: number,
    minElemsPerGroup: number,
    maxElemsPerGroup: number,
    maxNumberGroups: number,
    releaseDate: Date,
    description: string,
    title: string,
}

export class AssignmentDomain {
    constructor(
        id: number,
        classroomId: number,
        minElemsPerGroup: number,
        maxElemsPerGroup: number,
        maxNumberGroups: number,
        releaseDate: Date,
        description: string,
        title: string,
    ) {
        this.id = id
        this.classroomId = classroomId
        this.minElemsPerGroup = minElemsPerGroup
        this.maxElemsPerGroup = maxElemsPerGroup
        this.maxNumberGroups = maxNumberGroups
        this.releaseDate = releaseDate
        this.description = description
        this.title = title
    }
}