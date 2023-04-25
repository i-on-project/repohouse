import {fetchDelete, fetchGet, fetchPost} from "../siren/Fetch"
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
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.DELIVERY_KEY, Hypermedia.menuServices.menu)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,deliveryId:deliveryId})
        return await fetchGet<DeliveryDtoProperties>(href)
    }

    createDelivery = async (body: DeliveryBody) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        //TODO: Change this
        const response = await fetchPost<DeliveryDtoProperties>(link.href, body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    syncDelivery = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchPost<DeliveryDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    deleteDelivery = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchDelete<DeliveryDeletedDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    editDelivery = async (body: DeliveryBody) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        //TODO: Change this
        const response = await fetchPost<DeliveryDtoProperties>(link.href, body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }
}