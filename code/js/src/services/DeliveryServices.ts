import { navigationRepository, systemServices } from "../react-components"
import * as Hypermedia from "../http/Hypermedia"
import { fetchDelete, fetchGet, fetchPost } from "../http/Fetch"
import { SirenEntity } from "../http/Siren"
import { DeliveryBody, DeliveryDeletedDtoProperties, DeliveryDtoProperties } from "../domain/dto/DeliveryDtoProperties"
import { parse } from "uri-template"


export class DeliveryServices {

    delivery = async (courseId,classroomId,assignmentId,deliveryId) => {
        const link = await navigationRepository.ensureLink(Hypermedia.DELIVERY_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,deliveryId:deliveryId})
        return await fetchGet<DeliveryDtoProperties>(href)
    }

    createDelivery = async (body: DeliveryBody) => {
        const link = await navigationRepository.ensureLink(Hypermedia.COURSE_KEY, systemServices.home)
        //TODO: Change this
        const response = await fetchPost<DeliveryDtoProperties>(link.href, body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    syncDelivery = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.COURSE_KEY, systemServices.home)
        // TODO: Change this
        const response = await fetchPost<DeliveryDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    deleteDelivery = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.COURSE_KEY, systemServices.home)
        // TODO: Change this
        const response = await fetchDelete<DeliveryDeletedDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    editDelivery = async (body: DeliveryBody) => {
        const link = await navigationRepository.ensureLink(Hypermedia.COURSE_KEY, systemServices.home)
        //TODO: Change this
        const response = await fetchPost<DeliveryDtoProperties>(link.href, body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }
}
