export interface ErrorMessageModel{
    type: ResponseEntity<Problem>,
    title:string,
    detail:string,
    data:any
}

export class ErrorMessageModel{
    constructor(type:ResponseEntity<Problem>,title:string,detail:string,data:any = null){
        this.type=type;
        this.title=title;
        this.detail=detail;
        this.data=data;
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

