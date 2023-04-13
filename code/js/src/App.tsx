import * as React from 'react'
import {createBrowserRouter, RouterProvider, useParams} from 'react-router-dom'
import {authServices, courseServices, menuServices, systemServices} from './Dependecies'
import { ShowHomeFetch } from './Home'
import { ShowCreditsFetch } from './Credits'
import {AuthnContainer} from "./Auth";
import {RequireAuth} from "./RequiredAuth";
import {NavBarShow} from "./NavBar";
import {ShowAuthTeacherFetch} from "./AuthTeacher";
import {ShowAuthStudentFetch} from "./AuthStudent";
import {ShowAuthCallbackFetch} from "./AuthCallback";
import {Pending} from "@mui/icons-material";
import {ShowMenuFetch} from "./Menu";
import {ShowCourseCreateFetch, ShowCourseFetch} from "./Course";
import {ShowPendingTeacherFetch} from "./ApproveTeachers";

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
        {
            "path": "/courses/:courseId",
            "element": <RequireAuth>
                <Course/>
            </RequireAuth>
        },
        {
            "path": "/courses/create",
            "element": <RequireAuth>
                <CourseCreate/>
            </RequireAuth>
        },
        {
            "path": "/pending-teachers",
            "element": <RequireAuth>
                <PendingTeacher/>
            </RequireAuth>
        },
        {
            "path": "*",
            "element": <div>Not found</div>
        }
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
            <ShowMenuFetch menuServices={menuServices}/>
        </div>
    )
}

function Course() {
    const {courseId} = useParams<{ courseId: string }>();
    return (
        <div>
            <ShowCourseFetch courseServices={courseServices} courseId={Number(courseId)}/>
        </div>
    )
}

function CourseCreate() {
    return (
        <div>
            <ShowCourseCreateFetch courseServices={courseServices}/>
        </div>
    )
}

function PendingTeacher() {
    return (
        <div>
            <ShowPendingTeacherFetch menuServices={menuServices}/>
        </div>
    )
}

