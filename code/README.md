## Code

This folder contains all the source code of the project.

The i-on Project is composed of three main components:

- [Android application](android)
- [Web application](js)
- [JVM application](jvm)
- [Database](sql)


### Android

The Web application is the component that is used by the teachers to manage the system,
by accepting or declining students' requests that imply writing operations to the GitHub API.

The Android application is developed in Kotlin, using the Android Studio IDE.

### Web

The Web application is the componen that is used by the teachers and students to enroll in the system,
being the main visual component of the project.

The Web application is developed in TypeScript, using the React framework.

### JVM

The JVM exposes a REST API that is used by the Android and Web applications, with the purpose of
processing requests and interacting with the database and the external APIs.

The JVM application is developed in Kotlin, using the IntelliJ IDEA IDE.

### Database

The database is the component that stores all the information of the system, being used by the JVM application.

The database is developed in PostgreSQL, using the pgAdmin IDE.



