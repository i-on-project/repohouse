import { NavigationRepository } from './NavigationRepository'
import { SystemServices } from './services/SystemServices'
import {AuthServices} from "./services/AuthServices";
import {MenuServices} from "./services/MenuServices";
import {CourseServices} from "./services/CourseServices";
import {ClassroomServices} from "./services/ClassroomServices";
import {AssignmentServices} from "./services/AssignmentServices";
import {DeliveryServices} from "./services/DeliveryServices";
import {TeamServices} from "./services/TeamServices";

export const navigationRepository = new NavigationRepository()
export const systemServices = new SystemServices()
export const authServices = new AuthServices()
export const menuServices = new MenuServices()
export const courseServices = new CourseServices()
export const classroomServices = new ClassroomServices()
export const assignmentServices = new AssignmentServices()
export const deliveryServices = new DeliveryServices()
export const teamServices = new TeamServices()

export const HOME_KEY = "home"
export const CREDITS_KEY = "credits"
export const MENU_KEY = "menu"
export const AUTH_TEACHER_KEY = "authTeacher"
export const AUTH_STUDENT_KEY = "authStudent"
export const AUTH_REGISTER_INFO = "registerInfo"
export const AUTH_REGISTER_STUDENT = "registerStudent"
export const AUTH_REGISTER_TEACHER = "registerTeacher"
export const AUTH_STATUS_KEY = "status"
export const AUTH_STATE_KEY = "state"
export const AUTH_VERIFY_STUDENT_KEY = "verify"
export const LOGOUT_KEY = "logout"
export const ORGS_KEY = "orgs"
export const CREATE_COURSE_KEY = "createCourse"
export const COURSE_KEY = "course"
export const TEACHERS_APPROVAL_KEY = "teachersApproval"
export const APPROVE_TEACHERS_KEY = "approveTeacher"
export const CLASSROOM_KEY = "classroom"
export const ASSIGNMENT_KEY = "assignment"
export const ASSIGNMENTS_KEY = "assignments"
export const DELIVERY_KEY = "delivery"
export const DELIVERIES_KEY = "deliveries"
export const TEAM_KEY = "team"
export const TEAMS_KEY = "teams"
export const REQUESTS_KEY = "requestsHistory"
export const JOIN_TEAM_KEY = "joinTeam"
export const CREATE_TEAM_KEY = "createTeam"
export const DELETE_DELIVERY_KEY = "deleteDelivery"
export const SYNC_DELIVERY_KEY = "syncDelivery"
export const EXIT_TEAM_KEY = "leaveTeam"
export const POST_FEEDBACK_KEY = "postFeedback"
export const INVITE_CODE_KEY = "inviteCode"
export const LOCAL_COPY_KEY = "localCopy"
export const CREATE_CLASSROOM_KEY = "createClassroom"
export const ARCHIVE_CLASSROOM_KEY = "archiveClassroom"


export const systemLinkKeys = [
    HOME_KEY,
    CREDITS_KEY,
    MENU_KEY,
    AUTH_TEACHER_KEY,
    AUTH_STUDENT_KEY,
    AUTH_REGISTER_INFO,
    AUTH_STATUS_KEY,
    AUTH_STATE_KEY,
    ORGS_KEY,
    COURSE_KEY,
    CLASSROOM_KEY,
    ASSIGNMENT_KEY,
    ASSIGNMENTS_KEY,
    DELIVERY_KEY,
    DELIVERIES_KEY,
    TEAM_KEY,
    TEAMS_KEY,
    LOCAL_COPY_KEY,
    REQUESTS_KEY,
    TEACHERS_APPROVAL_KEY
]

export const systemActionKeys = [
    LOGOUT_KEY,
    AUTH_REGISTER_STUDENT,
    AUTH_REGISTER_TEACHER,
    AUTH_VERIFY_STUDENT_KEY,
    CREATE_COURSE_KEY,
    APPROVE_TEACHERS_KEY,
    JOIN_TEAM_KEY,
    CREATE_TEAM_KEY,
    DELETE_DELIVERY_KEY,
    SYNC_DELIVERY_KEY,
    EXIT_TEAM_KEY,
    POST_FEEDBACK_KEY,
    INVITE_CODE_KEY,
    CREATE_CLASSROOM_KEY,
    ARCHIVE_CLASSROOM_KEY
]
