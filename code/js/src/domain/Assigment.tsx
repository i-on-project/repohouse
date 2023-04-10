export interface AssigmentDomain {
    id: number,
    classroomId: number,
    maxElemsPerGroup: number,
    maxNumberGroups: number,
    releaseDate: Date,
    description: string,
    title: string,
}

export class AssigmentDomain {
    constructor(
        id: number,
        classroomId: number,
        maxElemsPerGroup: number,
        maxNumberGroups: number,
        releaseDate: Date,
        description: string,
        title: string,
    ) {
        this.id = id
        this.classroomId = classroomId
        this.maxElemsPerGroup = maxElemsPerGroup
        this.maxNumberGroups = maxNumberGroups
        this.releaseDate = releaseDate
        this.description = description
        this.title = title
    }
}