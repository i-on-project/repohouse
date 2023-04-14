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
import {ShowMenuFetch} from "./Menu";
import {ShowCourseCreateFetch, ShowCourseFetch} from "./Course";
import {ShowTeacherApprovalFetch} from "./ApproveTeachers";
import {ShowCreateCallbackFetch, ShowCreateFetch, ShowCreateTeacherFetchPost} from "./Create";
import {ShowStatusCallbackFetch, ShowStatusFetch} from "./Status";
import {ShowVerifyFetch} from "./Verify";

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
                "path": "/auth/create",
                "element": <Create/>
            },
            {
                "path": "/auth/create/callback",
                "element": <CreateCallback/>
            },
            {
                "path": "/auth/verify",
                "element": <Verify/>
            },
            {
                "path": "/auth/status",
                "element": <Status/>
            },
            {
                "path": "/auth/status/callback",
                "element": <StatusCallback/>
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
                    <TeacherApproval/>
                </RequireAuth>
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
function Create() {
    return (
        <div>
            <ShowCreateFetch authServices={authServices}/>
        </div>
    )
}

function CreateCallback() {
    return (
        <div>
            <ShowCreateCallbackFetch/>
        </div>
    )
}

function StatusCallback() {
    return (
        <div>
            <ShowStatusCallbackFetch/>
        </div>
    )
}

function Status() {
    return (
        <div>
            <ShowStatusFetch authServices={authServices}/>
        </div>
    )
}

function Verify() {
    return (
        <div>
            <ShowVerifyFetch authServices={authServices}/>
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

function TeacherApproval() {
    return (
        <div>
            <ShowTeacherApprovalFetch menuServices={menuServices}/>
        </div>
    )
}

