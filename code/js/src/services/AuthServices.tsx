import * as Hypermedia from "../Dependecies"
import {fetchGet, fetchPost} from "../siren/Fetch";
import {StatusDtoProperties} from "../domain/dto/StatusDtoProperties";
import {AuthRedirectDtoProperties} from "../domain/dto/AuthRedirectDtoProperties";
import { PendingUserDtoProperties } from "../domain/dto/PendingUserDtoProperties";


export class AuthServices {

    authTeacher = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_TEACHER_KEY, Hypermedia.systemServices.home)
        return await fetchGet<AuthRedirectDtoProperties>(link.href)
    }

    authStudent = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_STUDENT_KEY, Hypermedia.systemServices.home)
        return await fetchGet<AuthRedirectDtoProperties>(link.href)
    }

    getRegisterInfo = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_REGISTER_INFO, Hypermedia.systemServices.home)
        return await fetchGet<PendingUserDtoProperties>(link.href)
    }

    createTeacherPost = async () => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.AUTH_REGISTER_TEACHER, Hypermedia.systemServices.home)
        return await fetchPost<StatusDtoProperties>(link.href)
    }

    createStudentPost = async (schoolId:number) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.AUTH_REGISTER_STUDENT, Hypermedia.systemServices.home)
        const body = { schoolId: schoolId }
        return await fetchPost<StatusDtoProperties>(link.href, body)
    }

    verify = async (otp: { otp: number}) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.AUTH_VERIFY_STUDENT_KEY, Hypermedia.systemServices.home)
        return await fetchPost<StatusDtoProperties>(link.href, otp)
    }

    status = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_STATUS_KEY,Hypermedia.systemServices.home)
        return await fetchGet<StatusDtoProperties>(link.href)
    }

    async logout() {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.LOGOUT_KEY,Hypermedia.systemServices.home)
        return await fetchPost(link.href)
    }
}