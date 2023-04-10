import * as React from 'react'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import {authServices, systemServices} from './Dependecies'
import { ShowHomeFetch } from './Home'
import { ShowCreditsFetch } from './Credits'
import {AuthnContainer} from "./Auth";
import {RequireAuth} from "./RequiredAuth";
import {NavBarShow} from "./NavBar";

const router = createBrowserRouter([
    {
        "path": "/",
        "element": <NavBar/>,
        "children": [
            {
            "path": "/",
            "element": <Home/>
        },
        {
            "path": "/credits",
            "element": <Credits/>
        },
        {
            "path": "/auth/teacher",
            "element": <AuthTeacher/>
        },
        {
            "path": "/auth/student",
            "element": <AuthStudent/>
        },
        {
            "path": "/menu",
            "element": <RequireAuth>
                <Menu/>
            </RequireAuth>
        },
    ]}
])

export function App() {
    return (
        <AuthnContainer>
            <RouterProvider router={router}/>
        </AuthnContainer>
    )
}

function NavBar() {
    return (
        <div>
            <NavBarShow authServices={authServices}/>
        </div>
    )
}
function Home() {
    return (
        <div>
            <ShowHomeFetch systemServices={systemServices}/>
        </div>
    )
}

function Credits() {
    return (
        <div>
            <ShowCreditsFetch systemServices={systemServices}/>
        </div>
    )
}

function AuthTeacher() {
    return (
        <div>
            <h1>Not done</h1>
        </div>
    )
}

function AuthStudent() {
    return (
        <div>
            <h1>Not done</h1>
        </div>
    )
}

function Menu() {
    return (
        <div>
            <h1>Not done</h1>
        </div>
    )
}

