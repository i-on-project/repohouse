# Backend Service API and Hypermedia

**All API Hypermedia learned routes are obtained through both Web and Mobile home endpoints**.

### Media-Types

The Backend Service API makes use of two different media-types:
* `application/vnd.siren+json` : For representation of sucessful operations and Backend Service resources.
* `application/problem+json` : For representation of operations that resulted in a error.

### Hypermedia Specification

The adopted Hypermedia specification is [Siren](https://github.com/kevinswiber/siren).

Siren is a hypermedia format that allows the addition of metadata to the responses,
that can be used to navigate through the API.

> "Siren is a hypermedia specification for representing entities. 
> As HTML is used for visually representing documents on a Web site, 
> Siren is a specification for presenting entities via a Web API. 
> Siren offers structures to communicate information about entities, 
> actions for executing state transitions, and links for client navigation."
> 
> `Siren - Specification`

The API adopted Siren format is composed by the following fields:

- `class` {String}: The class of the entity. It can be a single class or a list of classes.
- `properties`: The properties of the entity [see [Hypermedia Properties Representations](#hypermedia-properties-representations)].
- `entities`: An Entity is a URI-addressable resource that has properties and actions associated with it. It may contain sub-entities and navigational links.
  - `properties`: A set of key-value pairs that describe the state of an entity. In JSON Siren, this is an object such as { "name": "Kevin", "age": 30 }. Optional.
  - `rel`: Defines the relationship of the sub-entity to its parent, per [Web Linking (RFC5988)](https://tools.ietf.org/html/rfc5988) and Link Relations. MUST be a non-empty array of strings. Required.
  - `links`: A collection of items that describe navigational links, distinct from entity relationships. Link items should contain a rel attribute to describe the relationship and an href attribute to point to the target URI. Entities should include a link rel to self. In JSON Siren, this is represented as "links": [{ "rel": ["self"], "href": "http://api/classroom/1234" }]. Optional.
- `actions`: The actions that can be performed on the entity.
  - `name` {String}: Identifies the action to be performed.
  - `method` {String}: The HTTP method of the action.
  - `href` {URI}: The URI of the action.
  - `title` {String}: Descriptive text about the action.
  - `type` {String}: The encoding type for the request.
  - `fields`: Fields represent controls inside actions.
    - `type` {String}: The input type of the field.
    - `value`: A value assigned to the field.
    - `title` {String}: Textual annotation of a field.
- `links`: Represent navigational transitions.
  - `rel` : List of relations of the link to its entity.
  - `href` {String}: The URI of the linked resource.
  - `needAuthentication` {Boolean}: Defines if access to the link requires API authentication.

### Hypermedia Error Representation

[Problem Json](https://datatracker.ietf.org/doc/html/rfc7807) is a standard specification to represent any error thrown by an API.

> The purpose of Problem is so that “API [consumers] can be informed of both the high-level error class (using the status code) and the finer-grained details of the problem”.

The API adopted Problem Json representation is composed by:

- `title` {String}: Short human-readable summary of the problem.
- `detail` {String}: Human-readable description of this specific problem.
- `type` {URI}: An absolute URL that leads to a page containing human-readable documentation regarding the problem.


### Hypermedia Properties Representations

#### Web

All web API routes are prefixed with `/api/web`.
The specific properties representation for each endpoint response are:

- GET ```/home```
    - title {String}: Project´s and system name.
    - description {String}: System´s main purpose.
    - subDescription {String}: Additional description.
    - est {String}: Eastern Stantard Time Year.
- GET ```/credits```
    - teacher
        - name {String}: Project´s teacher name.
        - email {String}: Project´s teacher email.
        - githubLink {String}: Project´s teacher github account URL.
    - students: list of students
        - name {String}: Project´s teacher institutional name.
        - email {String}: Project´s teacher institutional email.
        - githubLink {String}: Project´s teacher github account URL.
- GET ```/auth/teacher```
    - message {String}: Explains the next authentications step.
    - url {String}: Github´s OAuth Login Page URL containing the teacher scope.
- GET ```/auth/student```
    - message {String}: Explains the next authentications step.
    - url {String}: Github´s OAuth Login Page URL containing the student scope.                  
- GET ```/auth/state```
    - user {String}: Type of user.
    - authenticated {Boolean}: Indicates if user is authenticated in the API.
    - githubId {Number}: User Github Id.
    - userId {Number}: User System Id.                                    
- GET ```/auth/register```
    - name {String}: Username.
    - email {String}: User institutional email.
    - gitHubUsername {String}: User Github Name.            
- POST ```/auth/register/teacher```
    - statusInfo {String}: Registration Status.
    - message {String}: Explains the next authentications step.       
- POST ```/auth/register/student```
    - statusInfo {String}: Registration Status.
    - message {String}: Explains the next authentications step.            
- GET ```/auth/status```
    - statusInfo {String}: Registration Status.
    - message {String}: Explains the next authentications step.                    
- POST ```/auth/register/student/verify```
    - statusInfo {String}: Registration Status.
    - message {String}: Explains the next authentications step. 
- POST ```/auth/register/student/resend```
    - statusInfo {String}: Registration Status.
    - message {String}: Explains the next authentications step. 
- POST ```/auth/logout```
    - No properties (null).
- GET ```/menu```
    - name {String}: Username.
    - schoolNumber {Number}: If user is a student, student school number.
    - email {String}: User institutional email.
    - courses:
        - id {Number}: Course System´s Id.
        - orgUrl {String}: Course´s associated Github organization URL.
        - name {String}: Course´s associated Github organization name.
        - orgId {Number}: Course´s associated Github organization id.
        - teacher:
            - name {String}: Course´s teacher name.
            - email {String}: Course´s teacher institutional email.
            - id {Number}: Course´s teacher system´s id.
            - githubUsername {String}: Course´s teacher Github username.
            - githubId {Number}: Course´s teacher Github id..
            - isCreated {Boolean}: Indicates if the teacher is registered in the system.
- GET ```/teachers```
- POST ```/teachers```
- GET ```/courses/:id```
- GET ```/courses/create```
- POST ```/courses/create```
- PUT ```/courses/:id/leave```
- PUT ```/courses/:id```
- GET ```/orgs/```
- GET ```/courses/:id/classrooms/:id```
- POST ```/courses/:id/classrooms/create```
- PUT ```/courses/:id/classrooms/:id/archive```
- POST ```/courses/:id/classrooms/:id/edit``` 
- POST ```/courses/:id/enter-classroom/:inviteLink```
- POST ```/courses/:id/classrooms/:id/sync``` 
- POST ```/courses/:id/classrooms/:id/copy```
- GET ```/courses/:id/classrooms/:id/assignments/:id```
- POST ```/courses/:id/classrooms/:id/assignments/create```
- DELETE ```/courses/:id/classrooms/:id/assignments/:id/delete```
- GET ```/courses/:id/classrooms/:id/assignments/:id/deliveries/:id```          
- POST ```/courses/:id/classrooms/:id/assignments/:id/deliveries/create```                                         
- DELETE ```/courses/:id/classrooms/:id/assignments/:id/deliveries/:id/delete```                                    
- POST ```/courses/:id/classrooms/:id/assignments/:id/deliveries/:id/edit```     
- POST ```/courses/:id/classrooms/:id/assignments/:id/deliveries/:id/sync```
- GET ```/courses/:id/classrooms/:id/assignments/:id/team```               
- GET ```/courses/:id/classrooms/:id/assignments/:id/teams```              
- POST ```/courses/:id/classrooms/:id/assignments/:id/team/create```       
- POST ```/courses/:id/classrooms/:id/assignments/:id/team/join```         
- POST ```/courses/:id/classrooms/:id/assignments/:id/team/exit```         
- GET ```/courses/:id/classrooms/:id/assignments/:id/team/requests```      
- POST ```/courses/:id/classrooms/:id/assignments/:id/team/requests/:id```
- POST ```/courses/:id/classrooms/:id/assignments/:id/team/feedback```

#### Mobile

All mobile API routes are prefixed with `/api/mobile`.
The specific properties representation for each endpoint response are:

- GET ```/home```    
- GET ```/credits```
- GET ```/auth```          
- GET ```/auth/callback``` 
- GET ```/auth/token```    
- GET ```/menu```
- GET ```/courses/:id```     
- POST ```/courses/leave```             
- GET ```/courses/:id/classrooms/:id```
- POST ```/courses/:id/classrooms/archived``` 
- GET ```/courses/:id/classrooms/:id/assignments/:id```          
- GET ```/courses/:id/classrooms/:id/assignments/:id/team/:id/requests``` 
- POST ```/courses/:id/classrooms/:id/assignments/:id/team/:id/create```  
- PUT ```/courses/:id/classrooms/:id/assignments/:id/team/:id/requests```
- DELETE ```/courses/:id/classrooms/:id/assignments/:id/team/:id/requests/:id```
