export interface UserDomain {
    id: number
    name: string
    email: string
    githubUsername: string
    isCreated: Boolean
    githubId: bigint
    token: string
}


export interface Student extends UserDomain {
    schoolId: number,
}
export class Student implements UserDomain{
    constructor(
        id: number,
        name: string,
        email: string,
        githubUsername: string,
        isCreated: Boolean,
        githubId: bigint,
        token: string,
        schoolId: number,
    ) {
        this.id = id
        this.name = name
        this.email = email
        this.githubUsername = githubUsername
        this.isCreated = isCreated
        this.githubId = githubId
        this.token = token
        this.schoolId = schoolId
    }
}

export interface Teacher extends UserDomain {}

export class Teacher implements UserDomain {
    constructor(
        id: number,
        name: string,
        email: string,
        githubUsername: string,
        isCreated: Boolean,
        githubId: bigint,
        token: string,
    ) {
        this.id = id
        this.name = name
        this.email = email
        this.githubUsername = githubUsername
        this.isCreated = isCreated
        this.githubId = githubId
        this.token = token
    }
}