import {fetchGet, fetchPost} from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {MenuDtoProperties} from "../domain/dto/MenuDtoProperties";
import {CourseBody, CourseDtoProperties, CourseWithClassroomsDtoProperties} from "../domain/dto/CourseDtoProperties";
import {GitHubOrgsDtoProperties} from "../domain/dto/GitHubOrgsDtoProperties";
import {ClassroomDtoProperties} from "../domain/dto/ClassroomDtoProperties";


export class ClassroomServices {

    classroom = async (courseId,classroomId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchGet<ClassroomDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }


}