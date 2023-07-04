import { navigationRepository, systemServices } from "../react-components"
import * as Hypermedia from "../http/Hypermedia"
import { fetchGet, fetchPost, fetchPut } from "../http/Fetch"
import { ClassroomArchievedOrDeletedDtoProperties, ClassroomBody, ClassroomDtoProperties } from "../domain/dto/ClassroomDtoProperties"
import { parse } from "uri-template"


export class ClassroomServices {

    classroom = async (courseId,classroomId) => {
        const link = await navigationRepository.ensureLink(Hypermedia.CLASSROOM_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId})
        return await fetchGet<ClassroomDtoProperties>(href)
    }

    createClassroom = async (courseId,body:ClassroomBody) => {
        const link = await navigationRepository.ensureAction(Hypermedia.CREATE_CLASSROOM_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId})
        return await fetchPost<ClassroomDtoProperties>(href,body)
    }

    archiveClassroom = async (courseId,classroomId) => {
        const link = await navigationRepository.ensureAction(Hypermedia.ARCHIVE_CLASSROOM_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId})
        return await fetchPut<ClassroomArchievedOrDeletedDtoProperties>(href)
    }

    localCopy = async (courseId,classroomId) => {
        const link = await navigationRepository.ensureLink(Hypermedia.LOCAL_COPY_KEY, systemServices.home)
        return parse(link.href).expand({courseId: courseId, classroomId: classroomId})
    }

    leaveClassroom = async (courseId,classroomId) => {
        const link = await navigationRepository.ensureAction(Hypermedia.EXIT_CLASSROOM_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId})
        return await fetchPut<ClassroomDtoProperties>(href)
    }
}
