## Js

The application is created using [React](https://reactjs.org/) with [Typescript](https://www.typescriptlang.org/).

It uses [Webpack](#webpack) to bundle the application, 
to transpile Typescript to Javascript and to serve the application.

All the components are designed with [Material UI](https://material-ui.com/), to provide a better user experience.

## Table of Contents

- [Technologies](#technologies)
- [Architecture](#architecture)
  - [Components](#components)
  - [Services](#services)
  - [Http](#http)
  - [Hooks](#hooks)
  - [Router](#router)
- [Webpack](#webpack)
- [Environment variables](#environment-variables)
- [How to..](#how-to)
  - [Install](#install)
  - [Run](#run)
  - [Test](#test)


## Technologies

- [React](https://reactjs.org/)
- [Typescript](https://www.typescriptlang.org/)
- [Webpack](https://webpack.js.org/)
- [Jest](https://jestjs.io/)
- [Playwright](https://playwright.dev/)
- [React Router](https://reactrouter.com)
- [Material UI](https://material-ui.com/)

## Architecture

The application is organized in the following folders:

- [react-components/](#components) : React components for the application
- [services/](#services) : Services making the bridge between the components and the http
- [http/](#http) : Http client to interact with the backend
- domain/ : Domain objects
- utils/ : Utility constants and functions

### Components

The components are the building blocks of the application,
they are used to create the views of it and to interact with the user
they are designed to be reusable, and to be used in different views.

The components use the [Material UI](https://material-ui.com/) library,
to provide a better user experience.


### Services

The services are used to make the bridge between the components and the http client.

It's used to get the data from the React components, and the respective link or action,
and to create the respective http request, sending the request to the Http layer.

The services are a layer to process information before sending the request to the backend.

Tho the use of [JSON Home](https://datatracker.ietf.org/doc/html/draft-nottingham-json-home-06), the services have the knowledge of the link templates,
so it's chosen the correct link template to use, and the respective parameters to use.

### Http

The Http layer is used to send the requests to the backend, it's used to create the requests,
and to send them to the backend.

It makes the requests asynchronous, postponing the response to a later time, 
this is done to avoid blocking the UI, and to allow the user to continue using the application.

The Http layer is also used to handle the errors, and to parse the response,
returning the data to the services.

### Hooks

The hooks are used to provide a better user experience,
by providing a better way to handle the state of the application.

The hooks used are:

- `useAsync` : Used to handle asynchronous operations, such as the http requests
- `useParams` : Used to get the parameters from the URL
- `useNavigate` : Used to navigate to a different view
- `useLocation` : Used to get the current location

It's also used the `useEffect` hook, to handle side effects, such as the http requests,
and the `useCallback` hook, to memoize functions, to avoid unnecessary re-renders.

Has created a custom hook, `useFetch`, which is used to handle the http requests,
and some context hooks, `useLoggedIn`, `useGithubId` and ``useUser` to handle the user state, 
in the `AuthContainer` component created in the `Auth.tsx` file.


### Router

The React Dom Router is used to provide routing to the application,
providing dynamic routing.

It Allows creating a Single Page Application, with multiple views, using the browser
URL to determine which view to render, meaning
that the browser does not need to reload the page when the user navigates through the application.

All the routes are defined in the `App.tsx` file,
using `createBrowserRouter` to create a router,
listing all the routes in the `Routes` component.

```tsx
import { createBrowserRouter } from 'react-router-dom';

const Router = createBrowserRouter([
  {
    path: "/",
    element: <Home/>
  }
]);
```


Some common component used is the `Link`, 
which is used to navigate to a different view, 
without reloading the page, substituting the `a` tag.

```tsx
import { Link } from 'react-router-dom';

<Link to="/courses">Courses</Link>
```

### Webpack

Webpack is a module bundler, which is used to bundle the application,
allowing to bundle and optimize the application code, as well as
managing the dependencies.

It takes the application code and all the dependencies and generates
a single output file, which can then be server to the browser.

Webpack creates a dependency graph, which includes every module the application needs,
starting from the entry point, and recursively including all the dependencies.

It permits too to apply transformations to the code, such as transpiling Typescript to Javascript.

The configuration file is `webpack.config.js`.

For development, the `webpack-dev-server` is used, which is a development server,
and allows to create proxies to the backend, to avoid CORS issues, falling back to the backend
when the requested resource is not found in the frontend.

### Environment variables

The application only environment variable is the `PORT` variable, which is used to set the port
where the application is served, per default it's set to `3000`.

The `NGROK_URI` variable is only used for development purposes,
to set the backend URL, to avoid CORS issues.

Both variables are set in the `webpack.config.js` file.

### How to..

#### Install

To install the dependencies, run:

```
npm install
```

#### Run

To run the application, run:

```
npm start
```

#### Test

The application is tested with [Playwright](https://playwright.dev/),
which is a Node library to automate Chromium, Firefox and WebKit with a single API, 
running in [Jest](https://jestjs.io/) framework.

All the tests are located in the `tests/` folder.

Make sure the database is running before running the tests. Before running the tests, verify if the database is empty and run the script [script.sql](tests/script.sql).

To run the tests, you can run:

```bash
npx playwright test tests/
```

or for a more visual experience, you can run:

```bash
npx playwright test --ui tests/ 
```


