import { navigationRepository, systemServices } from "../react-components"
import * as Hypermedia from "../http/Hypermedia"
import { fetchGet, fetchPost } from "../http/Fetch"
import { StatusDtoProperties } from "../domain/dto/StatusDtoProperties"
import { AuthRedirectDtoProperties } from "../domain/dto/AuthRedirectDtoProperties"
import { PendingUserDtoProperties } from "../domain/dto/PendingUserDtoProperties"
import { StateDtoProperties } from "../domain/dto/AuthState"


export class AuthServices {

    authTeacher = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.AUTH_TEACHER_KEY, systemServices.home)
        return await fetchGet<AuthRedirectDtoProperties>(link.href)
    }

    authStudent = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.AUTH_STUDENT_KEY, systemServices.home)
        return await fetchGet<AuthRedirectDtoProperties>(link.href)
    }

    getRegisterInfo = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.AUTH_REGISTER_INFO, systemServices.home)
        return await fetchGet<PendingUserDtoProperties>(link.href)
    }

    createTeacherPost = async () => {
        const link = await navigationRepository.ensureAction(Hypermedia.AUTH_REGISTER_TEACHER, systemServices.home)
        return await fetchPost<StatusDtoProperties>(link.href)
    }

    createStudentPost = async (schoolId:number) => {
        const link = await navigationRepository.ensureAction(Hypermedia.AUTH_REGISTER_STUDENT, systemServices.home)
        const body = { schoolId: schoolId }
        return await fetchPost<StatusDtoProperties>(link.href, body)
    }

    verify = async (otp: { otp: number}) => {
        const link = await navigationRepository.ensureAction(Hypermedia.AUTH_VERIFY_STUDENT_KEY, systemServices.home)
        return await fetchPost<StatusDtoProperties>(link.href, otp)
    }

    status = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.AUTH_STATUS_KEY, systemServices.home)
        return await fetchGet<StatusDtoProperties>(link.href)
    }

    state = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.AUTH_STATE_KEY, systemServices.home)
        return await fetchGet<StateDtoProperties>(link.href)
    }

    resend = async () => {
        const link = await navigationRepository.ensureAction(Hypermedia.AUTH_RESEND_KEY, systemServices.home)
        return await fetchPost<StatusDtoProperties>(link.href)
    }

    logout = async () => {
        const link = await navigationRepository.ensureAction(Hypermedia.LOGOUT_KEY, systemServices.home)
        return await fetchPost(link.href)
    }
}
