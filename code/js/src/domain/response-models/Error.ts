export interface ErrorMessageModel{
    type: string,
    title: string,
    detail: string,
}

export class ErrorMessageModel{
    constructor(
        public type: string,
        public title: string,
        public detail: string,
    ) {
        this.type = type
        this.title = title
        this.detail = detail
    }
}
