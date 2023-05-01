import {fetchDelete, fetchGet, fetchPost, fetchPut} from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {MenuDtoProperties} from "../domain/dto/MenuDtoProperties";
import {CourseBody, CourseDtoProperties, CourseWithClassroomsDtoProperties} from "../domain/dto/CourseDtoProperties";
import {GitHubOrgsDtoProperties} from "../domain/dto/GitHubOrgsDtoProperties";
import {ClassroomDtoProperties} from "../domain/dto/ClassroomDtoProperties";
import {AssignmentDtoProperties} from "../domain/dto/AssignmentDtoProperties";
import {DeliveryBody, DeliveryDeletedDtoProperties, DeliveryDtoProperties} from "../domain/dto/DeliveryDtoProperties";
import {parse} from "uri-template";


export class DeliveryServices {

    delivery = async (courseId,classroomId,assignmentId,deliveryId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.DELIVERY_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,deliveryId:deliveryId})
        return await fetchGet<DeliveryDtoProperties>(href)
    }

    createDelivery = async (courseId,classroomId,assignmentId,body: DeliveryBody) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.CREATE_DELIVERY_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId:courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchPost<DeliveryDtoProperties>(href, body)
    }

    syncDelivery = async (courseId,classroomId,assignmentId,deliveryId) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.SYNC_DELIVERY_KEY,Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId:courseId,classroomId:classroomId,assignmentId:assignmentId,deliveryId:deliveryId})
        return await fetchPost<DeliveryDtoProperties>(href)
    }

    deleteDelivery = async (courseId,classroomId,assignmentId,deliveryId) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.DELETE_DELIVERY_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId:courseId,classroomId:classroomId,assignmentId:assignmentId,deliveryId:deliveryId})
        return await fetchDelete<DeliveryDeletedDtoProperties>(href)
    }

    editDelivery = async (courseId,classroomId,assignmentId,deliveryId,body: DeliveryBody) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.EDIT_DELIVERY_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId:courseId,classroomId:classroomId,assignmentId:assignmentId,deliveryId:deliveryId})
        return await fetchPost<DeliveryDtoProperties>(href, body)
    }
}