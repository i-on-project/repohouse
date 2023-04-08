export interface SirenLink{
    href:string,
    rel:string[],
    requiredAuthentication: boolean | null
}

export class SirenLink{
    constructor(
        href:string,
        rel:string[],
        requiredAuthentication: boolean | null = null
    ) {
        this.href = href
        this.rel = rel
        this.requiredAuthentication = requiredAuthentication
    }
}


export interface SirenAction{
    href:string,
    title:string | null,
    method: string | null,
    requiredAuthentication: boolean | null,
    fields: Field[] | null
}

export class SirenAction{
    constructor(
        href:string,
        title:string | null = null,
        method: string | null = null,
        requiredAuthentication: boolean | null = null,
        fields: Field[] | null = null
    ) {
        this.href = href
        this.title = title
        this.method = method
        this.requiredAuthentication = requiredAuthentication
        this.fields = fields
    }
}

interface Field{
    name:string,
    type:string | null,
    value:string | null
}

class Field{
    constructor(
        name:string,
        type:string | null = null,
        value:string | null = null
    ) {
        this.name = name
        this.type = null
        this.value = null
    }
}

export interface SirenEntity<T>{
    cls: string[] | null,
    properties: T | null,
    actions: SirenAction[] | null,
    links: SirenLink[] | null
}

export class SirenEntity<T>{
    constructor(
        cls:string[] | null = null,
        properties:T | null = null,
        actions: SirenAction[] | null = null,
        links: SirenLink[] | null = null
    ) {
        this.cls = cls
        this.properties = properties
        this.actions = actions
        this.links = links
    }
}

interface SubEntity{}

export interface EmbeddedLink extends SubEntity {
    href: string,
    rel: string[],
    requiredAuthentication: boolean |null
}

export class EmbeddedLink implements SubEntity{
    constructor(
        href: string,
        rel: string[],
        requiredAuthentication: boolean |null = null
    ) {
        this.href = href
        this.rel = rel
        this.requiredAuthentication = requiredAuthentication
    }
}


export interface EmbeddedEntity<T> extends SubEntity{
    cls: string[] | null,
    properties: T | null,
    actions: SirenAction[] | null,
    links: SirenLink[] | null
}

export class EmbeddedEntity<T> implements SubEntity{
    constructor(
        cls: string[] | null = null,
        properties: T | null = null,
        actions: SirenAction[] | null = null,
        links: SirenLink[] | null = null
    ) {
        this.cls = cls
        this.properties = properties
        this.actions = actions
        this.links = links
    }
}