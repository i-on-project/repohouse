import * as React from 'react'
import { createRoot } from 'react-dom/client'
import { App } from './App'
import { NavigationRepository } from '../http/NavigationRepository'
import { SystemServices } from '../services/SystemServices'
import { AuthServices } from "../services/AuthServices"
import { MenuServices } from "../services/MenuServices"
import { CourseServices } from "../services/CourseServices"
import { ClassroomServices } from "../services/ClassroomServices"
import { AssignmentServices } from "../services/AssignmentServices"
import { DeliveryServices } from "../services/DeliveryServices"
import { TeamServices } from "../services/TeamServices"
import { SirenEntity } from '../http/Siren'
import { AuthState } from './auth/Auth'

export const navigationRepository = new NavigationRepository()
export const systemServices = new SystemServices()
export const authServices = new AuthServices()
export const menuServices = new MenuServices()
export const courseServices = new CourseServices()
export const classroomServices = new ClassroomServices()
export const assignmentServices = new AssignmentServices()
export const deliveryServices = new DeliveryServices()
export const teamServices = new TeamServices()

async function launch() {
    let authState = AuthState.None
    await systemServices.home()
    const state = await authServices.state()
    if (state instanceof SirenEntity && state.properties.authenticated) {
        if (state.properties.user === "Student") authState = AuthState.Student
        if (state.properties.user === "Teacher") authState = AuthState.Teacher
    }
    const root = createRoot(document.getElementById("the-div"))
    root.render(
        <App authState={authState}/>
    )
}

launch()
