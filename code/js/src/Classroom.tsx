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
import {ClassroomServices} from "./services/ClassroomServices";

export function ShowClassroomFetch({
                                  classroomServices,courseId,classroomId
                              }: {
    classroomServices: ClassroomServices;
    courseId: number;
    classroomId: number;
}) {
    const content = useAsync(async () => {
        return await classroomServices.classroom(courseId,classroomId);
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
                        {"Last Sync Time: " + content.properties.lastSync}
                    </Typography>
                    <List>
                        {content.properties.assigments.map( assigment => (
                            <ListItem>
                                <Link to={"/courses/" + courseId  + "/classrooms/ "+ classroomId  + "/assigments/" + assigment.id}>
                                    {assigment.title + " - " + assigment.description}
                                </Link>
                            </ListItem>
                        ))}
                    </List>
                    <List>
                        {content.properties.students.map( student => (
                            <ListItem>
                                {student.name + " - " + student.schoolId}
                            </ListItem>
                        ))}
                    </List>
                    // TODO: Check if user is teacher and add button to create assgiment and to archive classroom
                </>
            ) : null}
        </div>
    );
}
