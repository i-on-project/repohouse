# API Routes

### WEB API Routes

All web API routes are prefixed with `/api/web`.

#### Common
- GET `./home` - Get the home page
- POST `./login` - Post to login
- POST `./logout` - Post to logout
- POST `./register` - Get to register
- GET `./credits` - Get the credits page
- GET `./courses` - Get the courses page
- GET `./courses/:id/classrooms` - Get the classrooms page from a course
- GET `./courses/:id/classrooms/:id` - Get the classroom page from a course
- GET `./courses/:id/classrooms/:id/teams/:id` - Get the team page from a classroom
- GET `./courses/:id/classrooms/:id/teams/:id/feedbacks` - Get the feedbacks page from a team
- GET `./courses/:id/classrooms/:id/assigments` - Get the assigments page from a classroom
- GET `./courses/:id/classrooms/:id/assigments/:id` - Get the assigment page from a classroom
- GET `./courses/:id/classrooms/:id/assigments/:id/deliveries/:id` - Get the delivery page from an assigment


#### Teacher

- GET `./login/teacher/approval` - Get the teacher approval status page
- POST `./courses/submit` - Submit a new course
- DELETE `./courses/:id` - Delete a course
- POST `./courses/:id/classrooms/submit` - Submit a new classroom
- DELETE `./courses/:id/classrooms/:id` - Delete a classroom
- POST `./courses/:id/classrooms/:id/assigments/submit` - Submit a new assigment
- DELETE `./courses/:id/classrooms/:id/assigments/:id` - Delete a assigment
- GET `./courses/:id/classrooms/:id/teams` - Get the teams page from a classroom
- DELETE `./courses/:id/classrooms/:id/teams/:id` - Delete a team
- POST `./courses/:id/classrooms/:id/teams/:id/feedbacks/submit` - Submit a new feedback
- DELETE `./courses/:id/classrooms/:id/teams/:id/feedbacks/:id` - Delete a feedback
- POST `./courses/:id/classrooms/:id/assigments/:id/deliveries/submit` - Submit a new delivery
- DELETE `./courses/:id/classrooms/:id/assigments/:id/deliveries/:id` - Delete a delivery
- POST `./courses/:id/classrooms/:id/sync` - Sync a classroom
- POST `./courses/:id/classrooms/:id/teams/:id/sync` - Sync a team


#### User

- GET `./login/student/approval` - Get the student approval status page
- POST `./courses/:id/classrooms/:id/teams/create` - Create a new team
- POST `./courses/:id/classrooms/:id/teams/leave` - Leave a team
- POST `./courses/:id/classrooms/:id/teams/enter` - Enter a team
- POST `./courses/:id/enter` - Enter a course
- POST `./courses/:id/leave` - Leave a course
- POST `./courses/:id/classrooms/:id/enter` - Enter a classroom
- POST `./courses/:id/classrooms/:id/leave` - Leave a classroom

### Mobile API Routes

All mobile API routes are prefixed with `/api/mobile`.


- GET `./home` - Get the home page
- POST `./login` - Post to login
- POST `./logout` - Post to logout
- GET `./credits` - Get the credits page
- GET `./teachers` - Get all teachers needing approval
- POST `./teachers/approve` - Approve a list of teachers
- GET `./courses` - Get all courses
- GET `./courses/:id/classrooms` - Get all classrooms from a course
- GET `./courses/:id/classrooms/:id` - Get all requests from a classroom
- GET `./courses/:id/classrooms/:id/teams` - Get all teams requests from a classroom
- POST `./courses/:id/classrooms/approve` - Approve a list of actions from a classroom
- POST `./courses/:id/classrooms/:id/teams/approve` - Approve a list of teams requests from a classroom
- POST `./courses/:id/classrooms/:id/sync` - Sync a classroom
- POST `./courses/:id/classrooms/:id/teams/:id/sync` - Sync a team



