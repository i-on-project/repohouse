import { NavigationRepository } from './NavigationRepository'
import { SystemServices } from './services/SystemServices'
import {AuthServices} from "./services/AuthServices";
import {MenuServices} from "./services/MenuServices";
import {CourseServices} from "./services/CourseServices";

export const navigationRepository = new NavigationRepository()
export const systemServices = new SystemServices()
export const authServices = new AuthServices()
export const menuServices = new MenuServices()
export const courseServices = new CourseServices()

export const HOME_KEY = "home"
export const CREDITS_KEY = "credits"
export const MENU_KEY = "menu"
export const AUTH_TEACHER_KEY = "authTeacher"
export const AUTH_STUDENT_KEY = "authStudent"
export const AUTH_CALLBACK_KEY = "authCallback"
export const LOGOUT_KEY = "logout"
export const TEACHERS_APPROVAL_KEY = "teachersApproval"
export const CREATE_COURSE_KEY = "createCourse"
export const COURSE_KEY = "course"