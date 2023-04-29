import * as React from "react";
import {useCallback, useState} from "react";
import {useAsync} from "./siren/Fetch";
import {ErrorMessageModel} from "./domain/response-models/Error";
import {SirenEntity} from "./siren/Siren";
import {List, ListItem, TextField, Typography} from "@mui/material";
import {Link, useNavigate} from "react-router-dom";
import {ClassroomServices} from "./services/ClassroomServices";
import {AuthState, useLoggedIn} from "./Auth";
import {Button} from "react-bootstrap";
import {ClassroomBody} from "./domain/dto/ClassroomDtoProperties";
import {ErrorAlert} from "./ErrorAlert";

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
    const user = useLoggedIn()
    const navigate = useNavigate();

    const handleCreateAssigment = useCallback(async () => {
        navigate("/courses/" + courseId + "/classrooms/" + classroomId + "/assigments/create");
    }, [navigate]);

    const handleArchiveClassroom = useCallback(async () => {
        const result = await classroomServices.archiveClassroom(courseId,classroomId);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            // TODO: navigate to the course page if deleted
        }
    }, [setError]);

    const handleLocalCopy = useCallback(async () => {
        const href = await classroomServices.localCopy(courseId, classroomId);
        window.open(href, "_blank");
    }, [setError]);

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
                        {"Last Sync Time: " + new Date(content.properties.lastSync).toLocaleString(
                            "en-GB",
                            {
                                hour: "2-digit",
                                minute: "2-digit",
                                month: "long",
                                day: "2-digit",
                                year: "numeric",
                            }
                        )}
                    </Typography>
                    <List>
                        {content.properties.assignments.map( assigment => (
                            <ListItem key={assigment.id}>
                                <Link to={"/courses/" + courseId  + "/classrooms/" + assigment.classroomId  + "/assignments/" + assigment.id}>
                                    {assigment.title + " - " + assigment.description}
                                </Link>
                            </ListItem>
                        ))}
                    </List>
                    <List>
                        {content.properties.students.map( student => (
                            <ListItem key={student.id}>
                                {student.name + " - " + student.schoolId}
                            </ListItem>
                        ))}
                    </List>
                    { user == AuthState.Teacher ? (
                        <>
                            <Button onClick={handleCreateAssigment}>Create Assigment</Button>
                            <Button onClick={handleArchiveClassroom}>Archive Classroom</Button>
                            <Button onClick={handleLocalCopy}>Local Copy</Button>
                        </>
                    ):null}
                </>
            ) : null}
        </div>
    );
}

export function ShowCreateClassroom({
                                  classroomServices,courseId
                              }: {
    classroomServices: ClassroomServices;
    courseId: number;
}) {
    const [error, setError] = useState<ErrorMessageModel>(null);
    const [name, setName] = useState<String>("");
    const navigate = useNavigate();

    const handleCreateClassroom = useCallback(async () => {
        if (name == "") return
        const body = new ClassroomBody(name)
        console.log(body)
        const result = await classroomServices.createClassroom(courseId,body);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            navigate("/courses/" + courseId + "/classrooms/" + result.properties.id);
        }
    }, [name,setError,navigate]);

    if (error) {
        return <ErrorAlert error={error} onClose={() => setError(null)}/>;
    }

    return (
        <div>
            <TextField
                label="Classroom Name"
                value={name}
                onChange={(e) => setName(e.target.value)}
            />
            <Button onClick={handleCreateClassroom}>Create Classroom</Button>
        </div>
    )
}
