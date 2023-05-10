import { navigationRepository, systemServices } from "../react-components"
import * as Hypermedia from "../http/Hypermedia"
import { fetchDelete, fetchGet, fetchPost } from "../http/Fetch"
import { DeliveryBody, DeliveryDeletedDtoProperties, DeliveryDtoProperties } from "../domain/dto/DeliveryDtoProperties"
import { parse } from "uri-template"


export class DeliveryServices {

    delivery = async (courseId,classroomId,assignmentId,deliveryId) => {
        const link = await navigationRepository.ensureLink(Hypermedia.DELIVERY_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,deliveryId:deliveryId})
        return await fetchGet<DeliveryDtoProperties>(href)
    }

    createDelivery = async (courseId, classroomId,assignmentId, body) => {
        const link = await navigationRepository.ensureAction(Hypermedia.CREATE_DELIVERY_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchPost<DeliveryDtoProperties>(href, body)
    }

    syncDelivery = async (courseId, classroomId,assignmentId,deliveryId) => {
        const link = await navigationRepository.ensureAction(Hypermedia.SYNC_DELIVERY_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,deliveryId:deliveryId})
        return await fetchPost<DeliveryDtoProperties>(href)
    }

    deleteDelivery = async (courseId, classroomId,assignmentId,deliveryId) => {
        const link = await navigationRepository.ensureAction(Hypermedia.DELETE_DELIVERY_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,deliveryId:deliveryId})
        return await fetchDelete<DeliveryDeletedDtoProperties>(href)
    }

    editDelivery = async (courseId, classroomId,assignmentId,deliveryId,body: DeliveryBody) => {
        const link = await navigationRepository.ensureAction(Hypermedia.EDIT_DELIVERY_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,deliveryId:deliveryId})
        return await fetchPost<DeliveryDtoProperties>(href, body)
    }
}
