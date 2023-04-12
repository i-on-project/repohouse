import * as React from 'react'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import {authServices, systemServices} from './Dependecies'
import { ShowHomeFetch } from './Home'
import { ShowCreditsFetch } from './Credits'
import {AuthnContainer} from "./Auth";
import {RequireAuth} from "./RequiredAuth";
import {NavBarShow} from "./NavBar";
import {ShowAuthTeacherFetch} from "./AuthTeacher";
import {ShowAuthStudentFetch} from "./AuthStudent";
import {ShowAuthCallbackFetch} from "./AuthCallback";

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
            "path": "/auth/callback",
            "element": <AuthCallback/>
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
            <ShowAuthTeacherFetch authServices={authServices}/>
        </div>
    )
}

function AuthStudent() {
    return (
        <div>
            <ShowAuthStudentFetch authServices={authServices}/>
        </div>
    )
}

function AuthCallback() {
    return (
        <div>
            <ShowAuthCallbackFetch authServices={authServices}/>
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

