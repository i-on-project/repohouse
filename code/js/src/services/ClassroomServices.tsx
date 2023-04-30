import {fetchGet, fetchPost, fetchPut} from "../siren/Fetch"
import * as Hypermedia from "../Dependecies"
import {
    ClassroomArchievedOrDeletedDtoProperties,
    ClassroomBody,
    ClassroomDtoProperties
} from "../domain/dto/ClassroomDtoProperties";
import {parse} from "uri-template";


export class ClassroomServices {

    classroom = async (courseId,classroomId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.CLASSROOM_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId})
        return await fetchGet<ClassroomDtoProperties>(href)
    }

    createClassroom = async (courseId,body:ClassroomBody) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.CREATE_CLASSROOM_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId})
        return await fetchPost<ClassroomDtoProperties>(href,body)
    }

    archiveClassroom = async (courseId,classroomId) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.ARCHIVE_CLASSROOM_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId})
        return await fetchPut<ClassroomArchievedOrDeletedDtoProperties>(href)
    }

    localCopy = async (courseId,classroomId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.LOCAL_COPY_KEY, Hypermedia.systemServices.home)
        return parse(link.href).expand({courseId: courseId, classroomId: classroomId})

    }
}