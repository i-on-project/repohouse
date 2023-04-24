import * as React from 'react'
import {createBrowserRouter, RouterProvider, useLocation, useParams} from 'react-router-dom'
import {
    assignmentServices,
    authServices,
    classroomServices,
    courseServices,
    deliveryServices,
    menuServices,
    systemServices,
    teamServices
} from './Dependecies'
import { ShowHomeFetch } from './Home'
import { ShowCreditsFetch } from './Credits'
import {AuthnContainer} from "./Auth";
import {RequireAuth} from "./RequiredAuth";
import {NavBarShow} from "./NavBar";
import {ShowAuthTeacherFetch} from "./AuthTeacher";
import {ShowAuthStudentFetch} from "./AuthStudent";
import {ShowMenuCallbackFetch, ShowMenuFetch} from "./Menu";
import {ShowCourseCreateFetch, ShowCourseCreatePost, ShowCourseFetch} from "./Course";
import {ShowTeacherApprovalFetch} from "./ApproveTeachers";
import {ShowCreateCallbackStudent, ShowCreateCallbackTeacher, ShowCreateStudentFetch, ShowCreateStudentFetchPost, ShowCreateTeacherFetch, ShowCreateTeacherFetchPost} from "./Create";
import {ShowStatusCallbackFetch, ShowStatusFetch} from "./Status";
import {ShowVerifyFetch} from "./Verify";
import { HandleAuthFail, HandleAuthFailCallback } from './AuthFail'
import {ShowClassroomFetch,ShowCreateClassroom} from "./Classroom";
import {ShowAssignmentFetch, ShowCreateAssignment} from './Assignment'
import {ShowCreateDelivery, ShowDeliveryFetch, ShowEditDelivery} from "./Delivery";
import {ShowTeamFetch, ShowTeamRequestsFetch} from "./Team";

const router = createBrowserRouter([
    {
        path: "/",
        element: <NavBar/>,
        children: [
            {
                path: "/", 
                element: <Home/>
            },
            {
                path: "/credits",
                element: <Credits/>
            },
            {
                path: "/auth/create/student",
                element: <CreateStudent/>
            },
            {
                path: "/auth/create/teacher",
                element: <CreateTeacher/>
            },
            {
                path: "/auth/register/student",
                element: <RegisterStudent/>
            },
            {
                path: "/auth/register/teacher",
                element: <RegisterTeacher/>
            },
            {
                path: "/auth/create/callback/student",
                element: <CreateCallbackStudent/>
            },
            {
                path: "/auth/create/callback/teacher",
                element: <CreateCallbackTeacher/>
            },
            {
                path: "/auth/verify",
                element: <Verify/>
            },
            {
                path: "/auth/status",
                element: <Status/>
            },
            {
                path: "/auth/status/callback",
                element: <StatusCallback/>
            },
            {
                path: "/auth/teacher",
                element: <AuthTeacher/>
            },
            {
                path: "/auth/student",
                element: <AuthStudent/>
            },
            {
                path: "/auth/fail",
                element: <AuthFail/>
            },
            {
                path: "/auth/fail/callback",
                element: <AuthFailCallback/>
            },
            {
                path: "/menu",
                element: <RequireAuth>
                    <Menu/>
                </RequireAuth>
            },
            {
                path: "/menu/callback/:user",
                element: <MenuCallback/>
            },
            {
                path: "/courses/:courseId",
                element: <RequireAuth>
                    <Course/>
                </RequireAuth>,
            },
            {
                path: "/courses/:courseId/classrooms/:classroomId",
                element: <RequireAuth>
                    <Classroom/>
                </RequireAuth>
            },
            {
                path: "/courses/:courseId/classrooms/:classroomId/assignments/:assignmentId",
                element: <RequireAuth>
                    <Assignment/>
                </RequireAuth>
            },
            {
                path: "deliveries/:deliveryId",
                element: <RequireAuth>
                    <Delivery/>
                </RequireAuth>
            },
            {
                path: "teams/:teamId",
                element: <RequireAuth>
                    <Team/>
                </RequireAuth>
            },
            {
                path: "requests",
                element: <RequireAuth>
                    <TeamRequests/>
                </RequireAuth>
            },
            {
                path: "edit",
                element: <RequireAuth>
                    <DeliveryEdit/>
                </RequireAuth>
            },
            {
                path: "deliveries/create",
                element: <RequireAuth>
                    <DeliveryCreate/>
                </RequireAuth>
            },
            {
                path: "assignments/create",
                element: <RequireAuth>
                    <AssignmentCreate/>
                </RequireAuth>
            },
            {
                path: "classrooms/create",
                element: <RequireAuth>
                    <ClassroomCreate/>
                </RequireAuth>
            },
            {
                path: "/teacher/orgs",
                element: <RequireAuth>
                    <CourseCreateFetch/>
                </RequireAuth>
            },
            {
                path: "/courses/create",
                element: <RequireAuth>
                    <CourseCreatePost/>
                </RequireAuth>
            },
            {
                path: "/pending-teachers",
                element: <RequireAuth>
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

function CreateStudent() {
    return (
        <div>
            <ShowCreateStudentFetch authServices={authServices}/>
        </div>
    )
}

function CreateTeacher() {
    return (
        <div>
            <ShowCreateTeacherFetch authServices={authServices}/>
        </div>
    )
}

function CreateCallbackStudent() {
    return (
        <div>
            <ShowCreateCallbackStudent/>
        </div>
    )
}

function CreateCallbackTeacher() {
    return (
        <div>
            <ShowCreateCallbackTeacher/>
        </div>
    )
}

function RegisterStudent() {
    return (
        <div>
            <ShowCreateStudentFetchPost authServices={authServices}/>
        </div>
    )
}

function RegisterTeacher() {
    return (
        <div>
            <ShowCreateTeacherFetchPost authServices={authServices}/>
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
            <ShowVerifyFetch authServices={authServices} error={null}/>
        </div>
    )
}

function AuthFail() {
    return (
        <div>
             <HandleAuthFail/>
        </div>
    )
}

function AuthFailCallback() {
    return (
        <div>
             <HandleAuthFailCallback/>
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

function MenuCallback(){
    return (
        <div>
            <ShowMenuCallbackFetch/>
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

function CourseCreateFetch() {
    return (
        <div>
            <ShowCourseCreateFetch courseServices={courseServices}/>
        </div>
    )
}

function CourseCreatePost() {
    return (
        <div>
            <ShowCourseCreatePost courseServices={courseServices}/>
        </div>
    )
}

function Classroom() {
    const {courseId, classroomId} = useParams<{ courseId: string, classroomId: string }>();
    console.log("courseId: " + courseId)
    return (
        <div>
            <ShowClassroomFetch classroomServices={classroomServices} classroomId={Number(classroomId)}
                                courseId={Number(courseId)}/>
        </div>
    )
}

function ClassroomCreate() {
    const {courseId} = useParams<{ courseId: string }>();
    return (
        <div>
            <ShowCreateClassroom classroomServices={classroomServices} courseId={Number(courseId)}/>
        </div>
    )
}

function Assignment() {
    const {courseId, classroomId, assignmentId} = useParams<{ courseId: string, classroomId: string, assignmentId: string }>();
    return (
        <div>
            <ShowAssignmentFetch assignmentServices={assignmentServices} />
        </div>
    )
}

function AssignmentCreate() {
    const {courseId, classroomId} = useParams<{ courseId: string, classroomId: string }>();
    return (
        <div>
            <ShowCreateAssignment assignmentServices={assignmentServices} classroomId={Number(classroomId)} error={null}/>
        </div>
    )
}

function Delivery() {
    const {courseId, classroomId, assignmentId, deliveryId} = useParams<{ courseId: string, classroomId: string, assignmentId: string, deliveryId: string }>();
    return (
        <div>
            <ShowDeliveryFetch deliveryServices={deliveryServices} />
        </div>
    )
}

function DeliveryCreate() {
    const {courseId, classroomId, assignmentId} = useParams<{ courseId: string, classroomId: string, assignmentId: string }>();
    return (
        <div>
            <ShowCreateDelivery deliveryServices={deliveryServices} assignmentId={Number(assignmentId)} error={null}/>
        </div>
    )
}

function DeliveryEdit() {
    const {courseId, classroomId, assignmentId, deliveryId} = useParams<{ courseId: string, classroomId: string, assignmentId: string, deliveryId: string }>();
    const delivery = useLocation().state.delivery
    return (
        <div>
            <ShowEditDelivery deliveryServices={deliveryServices} delivery={delivery} error={null}/>
        </div>
    )
}

function Team() {
    const {courseId, classroomId, teamId} = useParams<{ courseId: string, classroomId: string, teamId: string }>();
    return (
        <div>
            <ShowTeamFetch teamServices={teamServices}/>
        </div>
    )
}

function TeamRequests() {
    const {courseId, classroomId, teamId} = useParams<{ courseId: string, classroomId: string, teamId: string }>();
    return (
        <div>
            <ShowTeamRequestsFetch teamServices={teamServices}/>
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

