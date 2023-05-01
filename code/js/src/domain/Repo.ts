
export interface RepoDomain{
    id: number,
    url: string,
    name: string,
    isCreated: Boolean
}

export class RepoDomain {
    constructor(
        id: number,
        url: string,
        name: string,
        isCreated: Boolean,
    ) {
        this.id = id
        this.url = url
        this.name = name
        this.isCreated = isCreated
    }
}