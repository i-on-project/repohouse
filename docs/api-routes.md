# API Routes

### WEB API Routes

All web API routes are prefixed with `/api/web`.

<br/>

#### System Controller
| Route              | Description           |
|--------------------|-----------------------|
| GET ```/home```    | Get the home page.    |
| GET ```/credits``` | Get the credits page. |

<br/><br/>

#### Auth Controller
| Route                                    | Description                                                                |
|------------------------------------------|----------------------------------------------------------------------------|
| GET ```/auth/teacher```                  | Start GitHub OAuth process as Teacher.                                     |
| GET ```/auth/student```                  | Start GitHub OAuth process as Student.                                     | 
| GET ```/auth/state```                    | Get the current state of the user authentication.                          |
| GET ```/auth/callback```                 | Callback for GitHub OAuth process.                                         |
| GET ```/auth/register```                 | Get info of the user in progress of registration.                          |
| POST ```/auth/register/teacher```        | Register a new user as a teacher, pending verification by another teacher. |
| POST ```/auth/register/student```        | Register a new user as a student, pending verification from email.         |
| GET ```/auth/status```                   | Get the status of the current pending user.                                |
| POST ```/auth/register/student/verify``` | Verify a pending student using the sent OTP.                               |
| POST ```/auth/register/student/resend``` | Resend the OTP to the pending student.                                     |                             
| POST ```/auth/logout```                  | Logout the current user.                                                   |

<br/><br/>

#### Menu Controller

| Route                 | Description                               |
|-----------------------|-------------------------------------------|
| GET ```/menu```       | Get the menu for the current user.        |
| GET ```/teachers```   | Get the teacher needding approval page. * |
| POST ```/teachers```  | Approve a set of teachers. *              |

<br/><br/>

#### Course Controller

| Route                       | Description                                            |
|-----------------------------|--------------------------------------------------------|
| GET ```/courses/:id```      | Get the course page for the course with the given id.  |
| GET ```/courses/create```   | Get all teacher GitHub organizations. *                |
| POST ```/courses/create```  | Create a new course, based on a Github Organization. * |
| PUT ```/courses/:id/leave``` | Leave a course. **                                     |
| PUT ```/courses/:id```      | Archive a course. *                                    |
| GET ```/orgs/```            | Get all teacher GitHub organizations. *                |

<br/><br/>

#### Classroom Controller

| Route                                               | Description                                                 |
|-----------------------------------------------------|-------------------------------------------------------------|
| GET ```/courses/:id/classrooms/:id```               | Get the classroom page for the classroom with the given id. |
| POST ```/courses/:id/classrooms/create```           | Create a classroom. *                                       |
| PUT ```/courses/:id/classrooms/:id/archive```       | Archive a classroom. *                                      |
| POST ```/courses/:id/classrooms/:id/edit```         | Edit a classroom. *                                         |
| POST ```/courses/:id/enter-classroom/:inviteLink``` | Invite link to a student enter in a classroom. **           |
| POST ```/courses/:id/classrooms/:id/sync```         | Sync a classroom with GitHub. *                             |
| POST ```/courses/:id/classrooms/:id/copy```         | Create a local copy of classroom content. *                 |

<br/><br/>

#### Assignment Controller

| Route                                                           | Description                                                                 |
|-----------------------------------------------------------------|-----------------------------------------------------------------------------|
| GET ```/courses/:id/classrooms/:id/assignments/:id```           | Get the assignment page for the assignment with the given id.               |
| POST ```/courses/:id/classrooms/:id/assignments/create```      | Create a new assignment. *                                                  |
| DELETE ```/courses/:id/classrooms/:id/assignments/:id/delete``` | Delete an assignment. *                                                     |

<br/><br/>

#### Delivery Controller

| Route                                                                          | Description                                               |
|--------------------------------------------------------------------------------|-----------------------------------------------------------|
| GET ```/courses/:id/classrooms/:id/assignments/:id/deliveries/:id```           | Get the delivery page for the delivery with the given id. |
| POST ```/courses/:id/classrooms/:id/assignments/:id/deliveries/create```       | Create a new delivery. *                                  |
| DELETE ```/courses/:id/classrooms/:id/assignments/:id/deliveries/:id/delete``` | Delete a delivery. *                                      |
| POST ```/courses/:id/classrooms/:id/assignments/:id/deliveries/:id/edit```     | Edit a delivery. *                                        |
| POST ```/courses/:id/classrooms/:id/assignments/:id/deliveries/:id/sync```     | Sync a delivery with the Github repositories. *           |

<br/><br/>

#### Team Controller 

| Route                                                                    | Description                                           |
|--------------------------------------------------------------------------|-------------------------------------------------------|
| GET ```/courses/:id/classrooms/:id/assignments/:id/team```               | Get the team page for the delivery with the given id. |
| GET ```/courses/:id/classrooms/:id/assignments/:id/teams```              | Get all teams from an assignment.                     |
| POST ```/courses/:id/classrooms/:id/assignments/:id/team/create```       | Creates a request to create a new team. **            |
| POST ```/courses/:id/classrooms/:id/assignments/:id/team/join```         | Creates a request to Join a team. **                  |
| POST ```/courses/:id/classrooms/:id/assignments/:id/team/exit```         | Creates a request to Leave a team. **                 |
| GET ```/courses/:id/classrooms/:id/assignments/:id/team/requests```      | Get all requests history from a team.                 |
| POST ```/courses/:id/classrooms/:id/assignments/:id/team/requests/:id``` | Change a request status to 'pending' state. *         |
| POST ```/courses/:id/classrooms/:id/assignments/:id/team/feedback```     | Post a feedback in a team. *                          |

<br/><br/>

``` 
 * = Only for teachers.    
 ** = Only for students.
```

### Mobile API Routes

All mobile API routes are prefixed with `/api/mobile`.

<br/>

#### System Controller

| Route              | Description           |
|--------------------|-----------------------|
| GET ```/home```    | Get the home page.    |
| GET ```/credits``` | Get the credits page. |

<br/><br/>

#### Auth Controller

| Route                    | Description                                                                |
|--------------------------|----------------------------------------------------------------------------|
| GET ```/auth```          | Start GitHub OAuth process.                                                |
| GET ```/auth/callback``` | Callback for GitHub OAuth process.                                         |
| GET ```/auth/token```    | Obtain a token for the current user.                                       |


<br/><br/>

#### Menu Controller

| Route                 | Description                               |
|-----------------------|-------------------------------------------|
| GET ```/menu```       | Get the menu for the current user.        |

<br/><br/>

#### Course Controller

| Route                       | Description                                           |
|-----------------------------|-------------------------------------------------------|
| GET ```/courses/:id```      | Get the course page for the course with the given id. |
| POST ```/courses/leave```   | Accept a request to leave the course.                 |

<br/><br/>

#### Classroom Controller

| Route                                 | Description                                                 |
|---------------------------------------|-------------------------------------------------------------|
| GET ```/courses/:id/classrooms/:id``` | Get the classroom page for the classroom with the given id. |
| POST ```/courses/:id/classrooms/archived``` | Update the archived status of a classroom. |

<br/><br/>

#### Assignment Controller

| Route                                                           | Description                                                                 |
|-----------------------------------------------------------------|-----------------------------------------------------------------------------|
| GET ```/courses/:id/classrooms/:id/assignments/:id```           | Get the assignment page for the assignment with the given id.               |


<br/><br/>


#### Team Controller

| Route                                                                   | Description                                |
|-------------------------------------------------------------------------|--------------------------------------------|
| GET ```/courses/:id/classrooms/:id/assignments/:id/team/:id/requests``` | Get all teams requests pending acceptance. |
| POST ```/courses/:id/classrooms/:id/assignments/:id/team/:id/create```  | Update a team creation state.              |
| PUT ```/courses/:id/classrooms/:id/assignments/:id/team/:id/requests``` | Update all teams requests pending acceptance.              |
| DELETE ```/courses/:id/classrooms/:id/assignments/:id/team/:id/requests/:id``` | Delete a team request.              |