export interface Error{
    state: number,
    error: string,
    message: string,
    data: any
}

export class ErrorMessageModel{
    constructor(
        public state: number,
        public error: string,
        public message: string,
        public data: any
    ) {
        this.state = state;
        this.error = error;
        this.message = message;
        this.data = data;
    }
}

interface Problem{
    typeUri:string
}

class Problem {
    constructor(typeUri:string){
        this.typeUri = typeUri;
    }
}


interface ResponseEntity<T>{
    statusCodeValue:number,
    statusCode:string,
    headers:ObjHeaders,
    body: ObjBody

}

class ResponseEntity<T>{
    constructor(statusCodeValue:number,statusCode:string,headers:ObjHeaders,body: ObjBody){
        this.statusCodeValue=statusCodeValue;
        this.statusCode=statusCode;
        this.headers=headers;
        this.body=body;
    }
}


interface ObjHeaders{
    ContentType:String[]
}

interface ObjBody{
    type:string
}

