# User Flow

> The experience flow is critical to the success of an application,
> as good usability can ensure user satisfaction and loyalty.\
> It can help identify possible problems in the user experience,
> allowing improvements to be made to the interface or the *design* of the application.

## Flow

The flow of the application is initiated by both user categories _'Docente'_
and _'Aluno'_, where they, through the main page,
move to the authentication location to be assigned between what the user's role in the application is,
done at the time of user registration.

The entire registration and authorization process is done by **OAuth 2.0** connected to the GitHub API.
This section of flow can be skipped after there is the information in **cache** about the user in question.

The application is divided into a *web* component and a *mobile* component,
where the *web* component can be accessed by any user, 
while the mobile component can only be used by _'Docentes'_.

![Web User Flow](../docs/img/web-user-flow.png)*Web User Flow*

![Mobile User Flow](../docs/img/mobile-user-flow.png)*Mobile User Flow*

### Teacher

Entering the application as _'Docente'_,
the user experience goes through an initial menu where the teacher can access all the classes he teaches,
where navigation to the contents of each class is allowed.


In each class you have access to the published, unpublished and even created statements, 
so that students can access their publication and the list of groups, created by the students, 
and then obtain both their information and the content made through the repositories.

![Detailed Web Teacher Flow](../docs/img/web-teacher-flow.png) *Detailed Web Teacher Flow*

For a teacher to register in the application, after integration with GitHub, they must submit their name and email,
so that another teacher can verify them as a teacher, to allow them to have an application that is trusted by its users.


All the operations that require write operations are done through the *mobile* component,
where the teacher stores his token that allows him to make requests over the GitHub API.

Any request that requires authentication performed by the *web* component, either by the teacher or by the students, 
is added to a task list 
where it is then only performed by the mobile component through the teacher's token stored in the mobile device.
This component also serves for synchronization between the application data and GitHub.

![Detailed Mobile Teacher Flow](../docs/img/mobile-flow.png) *Detailed Mobile Teacher Flow*

### Student

A user identified as _'Aluno'_, may have two categories of user experience, a common and usual one:

Where the student when starting the application, will have access to all the disciplines to which he is enrolled, where he can then in each of them check can have access to a section with all the works already exposed by the teacher, and can then go to the detail of each one; as can have access to the GitHub repository, on which where the works should be performed.

![Detailed User Flow](../docs/img/web-student-flow.png) *Detailed User Flow*

And another way of user experience that a student can experience is through an invitation to join a course, 
where he can create or join a group and be directed to it, where he will then have access to its repository.

When the student starts the application through an invitation, he will access a page where he will be asked if he wants to join the course, and after authentication if required, will be redirected to a page where he can create a group or join one, and after his choice, will be redirected to the course repository.

![Detailed User Invite Flow](../docs/img/web-invite-student-flow.png) *Detailed User Invite Flow*


If you have already signed in to a group and want to backtrack in the user flow, 
you will be redirected to the beginning of the previous flow, to your course page.