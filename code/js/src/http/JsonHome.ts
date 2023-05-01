
export class JsonHome {
    constructor(
        api: API,
        resources: Resource[]
    ) {
        this.api = api
        this.resources = resources
    }
}

export interface JsonHome {
    api: API,
    resources: Resource[]
}

export class API {
    constructor(
        title: string,
        links: APILinks
    ) {
        this.title = title
        this.links = links
    }
}

export interface API {
    title: string,
    links: APILinks
}

export class APILinks {
    constructor(
        author: string[],
        describedBy: string
    ) {
        this.author = author
        this.describedBy = describedBy
    }
}

export interface APILinks {
    author: string[],
    describedBy: string
}

export class Resource {
    constructor(
        tag: string,
        hrefTemplate: string,
        hints: Hints
    ) {
        this.tag = tag
        this.hrefTemplate = hrefTemplate
        this.hints = hints
    }
}

export interface Resource {
    tag: string
    hrefTemplate: string,
    hints: Hints
}

export class Hints {
    constructor(
        allow: string[],
        format: string
    ) {
        this.allow = allow
        this.format = format
    }
}

export interface Hints {
    allow: string[],
    format: string
}
