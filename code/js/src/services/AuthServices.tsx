import * as Hypermedia from "../Dependecies"
import {fetchGet, fetchPost} from "../siren/Fetch";
import {SirenEntity} from "../siren/Siren";
import {StatusDtoProperties} from "../domain/dto/StatusDtoProperties";
import {AuthRedirectDtoProperties} from "../domain/dto/AuthRedirectDtoProperties";
import { PendingUserDtoProperties } from "../domain/dto/PendingUserDtoProperties";


export class AuthServices {

    authTeacher = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_TEACHER_KEY, Hypermedia.systemServices.home)
        const response = await fetchGet<AuthRedirectDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            //TODO
        }
        return response
    }

    authStudent = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_STUDENT_KEY, Hypermedia.systemServices.home)
        const response = await fetchGet<AuthRedirectDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            //TODO
        }
        return response
    }

    getRegisterInfo = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_REGISTER_INFO, Hypermedia.systemServices.home)
        const response = await fetchGet<PendingUserDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            //TODO
        }
        return response
    }

    createTeacherPost = async () => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.AUTH_REGISTER_TEACHER, Hypermedia.systemServices.home)
        const response = await fetchPost<StatusDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            //TODO
        }
        return response
    }

    createStudentPost = async (schoolId:number) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.AUTH_REGISTER_STUDENT, Hypermedia.systemServices.home)
        const body = { schoolId: schoolId }
        const response = await fetchPost<StatusDtoProperties>(link.href, body)
        if (response instanceof SirenEntity) {
            //TODO
        }
        return response
    }

    verify = async (otp: { otp: number}) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.AUTH_VERIFY_STUDENT_KEY, Hypermedia.systemServices.home)
        const response = await fetchPost<StatusDtoProperties>(link.href, otp)
        if (response instanceof SirenEntity) {
            //TODO
        }
        return response
    }

    status = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_STATUS_KEY,Hypermedia.systemServices.home)
        const response = await fetchGet<StatusDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            //TODO
        }
        return response
    }

    async logout() {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.LOGOUT_KEY,Hypermedia.systemServices.home)
        const response = await fetchPost(link.href)
        if (response instanceof SirenEntity) {
            //TODO
        }
        return response
    }
}