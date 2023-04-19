import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useCallback, useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import {List, ListItem, Select, Typography} from "@mui/material";
import {CourseServices} from "./services/CourseServices";
import {Form, Link, Navigate} from "react-router-dom";
import {CourseBody} from "./domain/dto/CourseDtoProperties";
import {Label} from "@mui/icons-material";
import {Button, Image} from "react-bootstrap";

export function ShowCourseFetch({
                                  courseServices,courseId
                              }: {
    courseServices: CourseServices;
    courseId: number;
}) {
    const content = useAsync(async () => {
        return await courseServices.course(courseId);
    });
    const [error, setError] = useState<ErrorMessageModel>(null);

    if (!content) {
        return (
            <Typography
                variant="h6"
                gutterBottom
            >
                ...loading...
            </Typography>
        );
    }

    if (content instanceof ErrorMessageModel && !error) {
        setError(content);
    }

    return (
        <div
            style={{
                alignItems: "center",
                justifyContent: "space-evenly",
            }}
        >
            {content instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h2"
                    >
                        {content.properties.name}
                    </Typography>
                    <Typography
                        variant="h6"
                        gutterBottom
                    >
                        {"Teacher: " + content.properties.teacher}
                    </Typography>
                    <a href={content.properties.orgUrl} target={"_blank"}>GitHub Page</a>
                    <List>
                        {content.properties.classrooms.map( classroom => (
                            <ListItem>
                                //TODO: If archived,make it grey
                                <Link to={"/courses/:id/classrooms/" + classroom.id}>
                                    {classroom.name}
                                </Link>
                            </ListItem>
                        ))}
                    </List>
                    // TODO: If teacher add button to create classroom if course not archived
                </>
            ) : null}
        </div>
    );
}

export function ShowCourseCreateFetch({
                                          courseServices
                                      }: {
    courseServices: CourseServices
}) {

    const content = useAsync(async () => {
        return await courseServices.getTeacherOrgs();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const [courseBody, setCourseBody] = useState<CourseBody | null>(null)

    const handleChange = useCallback((event:any) => {
        if (content instanceof SirenEntity) {
            const org = content.properties.orgs.find(org => org.url === event.target.value)
            setCourseBody(new CourseBody(org.login, org.url))
        }
    },[courseBody])

    const handleSubmit = useCallback((event:any) => {
        event.preventDefault()
        const course = courseBody
        if (!course) return
        //TODO : Change this
        ShowCourseCreatePost({courseServices, course})
    },[courseBody])

    if (!content) {
        return (
            <Typography
                variant="h6"
                gutterBottom
            >
                ...loading...
            </Typography>
        );
    }

    if (content instanceof ErrorMessageModel && !error) {
        setError(content);
    }

    return (
        <div
            style={{
                alignItems: "center",
                justifyContent: "space-evenly",
            }}
        >
            {content instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h2"
                    >
                        {"Create Course"}
                    </Typography>
                    <Form>
                        <Label>
                            Organization:
                            <select onChange={handleChange}>
                                {content.properties.orgs.map(org => (
                                    <option value={org.url}>
                                        {org.login}
                                        <Image>
                                            src={org.avatar_url}
                                        </Image>
                                    </option>
                                ))}
                            </select>
                        </Label>
                        <Button onClick={handleSubmit}>
                            {"Submit"}
                        </Button>
                    </Form>
                </>
            ) : null}
        </div>
    );
}

export function ShowCourseCreatePost({ courseServices, course }: { courseServices: CourseServices, course: CourseBody }) {
    const content = useAsync(async () => {
        return await courseServices.createCourse(course) })

    if (!content) {
        return (
            <p>...loading...</p>
        )
    }

    if (content instanceof ErrorMessageModel) {
        // TODO
    }

    return <Navigate to={"/courses"}/>
}