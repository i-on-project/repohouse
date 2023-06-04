## Android

The application was created using [Kotlin](https://kotlinlang.org/).

It uses [Gradle](https://gradle.org/) to build the application.

All the components are designed with [Jetpack Compose](https://developer.android.com/jetpack/compose), to provide a better user experience.

## Table of Contents

- [Technologies](#technologies)
- [Architecture](#architecture)
  - [Presentation](#presentation)
  - [DataAccess](#data-access)
  - [Http](#http)
- [Environment variables](#environment-variables)
- [How to..](#how-to)
  - [Build](#build)
  - [Run](#run)
  - [Test](#test)


## Technologies

- [Kotlin](https://kotlinlang.org/)
- [Gradle](https://gradle.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [GitHub API](https://docs.github.com/en/rest)
- [Custom Tabs](https://developer.chrome.com/docs/android/custom-tabs/overview/)
- [OKHttp](https://square.github.io/okhttp/)


## Architecture

The application is organized in the following folders:

- [presentation/](#presentation) : Application components with logic and UI
- [data-access/](#data-access) : Component to store and retrieve user session and information data
- [http/](#http) : Http client to interact with the backend
- domain/ : Domain objects
- ui/ : Utility constants and functions

### Presentation

The presentation layer is ensured by 3 main tasks:

- To provide the UI to the user through the Jetpack Compose components and navigation between the different screens of the application
- To provide the services to the user, to interact with the application and send the requests to the Http layer
- To provide the logic to the application, to handle the data and the user actions

This layer is the mediator between the user and the application, it is the layer that the user interacts with, ensuring the application functionality
is presented to the user in a way that is intuitive and visually appealing.


### Data Access

The data access layer is used to store and retrieve the user session and information data.

It has the capability to encrypt and decrypt the secret from authentication process, and stores in the device the access token needed to make the requests to the GitHub API.
This layer is used for the authentication process, to store the user session data, and to store the user information data.

It stores to the cookie needed to make the requests to the i-on ClassCode application.

### Http

The HTTP layer was created to generalize the http requests, and to provide a simple way to make them.

The API makes use of hypermedia, so the application needs to ensure that have the links and actions for that.
For every request made, the application needs to check if the response has the links and actions needed, and if it has, it needs to store them, to be used in the next requests, if not, it needs to make the request to the link provided by the response.

The layer has 2 methods to make the requests, because the application makes use of hypermedia, so there is a method for the requests to the application and other for the requests to the GitHub API.

## Environment variables

The only environment variable used by the application is the url needed to make the http requests.

The environment variable is named `NGROK_URI` and it is defined in the local properties file `local.properties`, that is located in the root of the project.
The file must include the `sdk.dir` property, that is the path to the Android SDK.


## How to..

### Build

To build the Android application, run the following command:

```
./gradlew assembleDebug
```

### Run

To run the Android application, run the following command:

```
./gradlew installDebug
```

### Test

[ ] TODO: Add tests implementation
