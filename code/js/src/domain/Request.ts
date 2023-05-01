

export interface RequestDomain {
    id: number
    creator: number
    state: String
    composite: number | null
}

export interface Apply extends RequestDomain {}
export class Apply implements RequestDomain {
    constructor(
        id: number,
        creator: number,
        state: String,
        composite: number | null,
    ) {
        this.id = id
        this.creator = creator
        this.state = state
        this.composite = composite
    }
}

export interface ArchiveRepo extends RequestDomain {
    repoId: number
}
export class ArchiveRepo implements RequestDomain {
    constructor(
        id: number,
        creator: number,
        state: String,
        composite: number | null,
        repoId: number,
    ) {
        this.id = id
        this.creator = creator
        this.state = state
        this.composite = composite
        this.repoId = repoId
    }
}

export interface Composite extends RequestDomain {
    requests: number[]
}
export class Composite implements RequestDomain {
    constructor(
        id: number,
        creator: number,
        state: String,
        composite: number | null,
        requests: number[],
    ) {
        this.id = id
        this.creator = creator
        this.state = state
        this.composite = composite
        this.requests = requests
    }
}

export interface CreateRepo extends RequestDomain {
    teamId: number
}
export class CreateRepo implements RequestDomain {
    constructor(
        id: number,
        creator: number,
        state: String,
        composite: number | null,
        teamId: number,
    ) {
        this.id = id
        this.creator = creator
        this.state = state
        this.composite = composite
        this.teamId = teamId
    }
}

export interface CreateTeam extends RequestDomain {}
export class CreateTeam implements RequestDomain {
    constructor(
        id: number,
        creator: number,
        state: String,
        composite: number | null
    ) {
        this.id = id
        this.creator = creator
        this.state = state
        this.composite = composite
    }
}

export interface JoinTeam extends RequestDomain {
    teamId: number
}
export class JoinTeam implements RequestDomain {
    constructor(
        id: number,
        creator: number,
        state: String,
        composite: number | null,
        teamId: number,
    ) {
        this.id = id
        this.creator = creator
        this.state = state
        this.composite = composite
        this.teamId = teamId
    }
}

export interface LeaveCourse extends RequestDomain {
    courseId: number
}
export class LeaveCourse implements RequestDomain {
    constructor(
        id: number,
        creator: number,
        state: String,
        composite: number | null,
        courseId: number,
    ) {
        this.id = id
        this.creator = creator
        this.state = state
        this.composite = composite
        this.courseId = courseId
    }
}

export interface LeaveTeam extends RequestDomain {
    teamId: number
}
export class LeaveTeam implements RequestDomain {
    constructor(
        id: number,
        creator: number,
        state: String,
        composite: number | null,
        teamId: number,
    ) {
        this.id = id
        this.creator = creator
        this.state = state
        this.composite = composite
        this.teamId = teamId
    }
}