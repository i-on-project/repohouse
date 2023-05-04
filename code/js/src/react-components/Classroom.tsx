import * as React from "react";
import {useCallback, useState} from "react";
import {useAsync} from "../http/Fetch";
import {ErrorMessageModel} from "../domain/response-models/Error";
import {SirenEntity} from "../http/Siren";
import {Backdrop, CircularProgress, List, ListItem, TextField, Typography} from "@mui/material";
import {Link, useNavigate} from "react-router-dom";
import {ClassroomServices} from "../services/ClassroomServices";
import {AuthState, useLoggedIn} from "./auth/Auth";
import {Button} from "react-bootstrap";
import {ClassroomBody} from "../domain/dto/ClassroomDtoProperties";
import {ErrorAlert} from "./error/ErrorAlert";

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
        navigate("/courses/" + courseId + "/classrooms/" + classroomId + "/assignments/create");
    }, [navigate]);

    const handleArchiveClassroom = useCallback(async () => {
        const result = await classroomServices.archiveClassroom(courseId,classroomId);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            if (result.properties.deleted) {
                navigate("/courses/" + courseId);
            }
        }
    }, [setError]);

    const handleLocalCopy = useCallback(async () => {
        const href = await classroomServices.localCopy(courseId, classroomId);
        window.open(href, "_blank");
    }, [setError]);

    if (!content) {
        return (
            <Backdrop
                sx={{ color: 'primary', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                open={true}
            >
                <CircularProgress color="primary" />
            </Backdrop>
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
                            { !content.properties.isArchived ? (
                                <>
                                    <Button onClick={handleCreateAssigment}>Create Assigment</Button>
                                    <Button onClick={handleArchiveClassroom}>Archive Classroom</Button>
                                </>
                            ):null}
                            <Button onClick={handleLocalCopy}>Local Copy</Button>
                        </>
                    ):null}
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => setError(null)}/>
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
        const result = await classroomServices.createClassroom(courseId,body);
        console.log("result")
        console.log(result)
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            navigate("/courses/" + courseId + "/classrooms/" + result.properties.id);
        }
    }, [name,setError,navigate]);


    return (
        <div>
            <TextField
                label="Classroom Name"
                value={name}
                onChange={(e) => setName(e.target.value)}
            />
            <Button onClick={handleCreateClassroom}>Create Classroom</Button>
            <ErrorAlert error={error} onClose={() => setError(null)}/>;
        </div>
    )
}
