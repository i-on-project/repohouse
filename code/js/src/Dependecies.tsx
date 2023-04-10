import { NavigationRepository } from './NavigationRepository'
import { SystemServices } from './services/SystemServices'
import {AuthServices} from "./services/AuthServices";

export const navigationRepository = new NavigationRepository()
export const systemServices = new SystemServices()
export const authServices = new AuthServices()

export const HOME_KEY = "home"
export const CREDITS_KEY = "credits"
export const MENU_KEY = "menu"
export const AUTH_TEACHER_KEY = "authTeacher"
export const AUTH_STUDENT_KEY = "authStudent"
export const LOGOUT_KEY = "logout"
export const TEACHERS_APPROVAL_KEY = "teachersApproval"
export const CREATE_COURSE_KEY = "createCourse"
export const COURSE_KEY = "course"