import * as React from "react";
import {useCallback, useState} from "react";
import {useAsync} from "../http/Fetch";
import {ErrorMessageModel} from "../domain/response-models/Error";
import {SirenEntity} from "../http/Siren";
import {Backdrop, Box, Button,CircularProgress, Grid, List, ListItem, ListSubheader, TextField, Typography} from "@mui/material";
import {Link, useNavigate} from "react-router-dom";
import {ClassroomServices} from "../services/ClassroomServices";
import {AuthState, useLoggedIn} from "./auth/Auth";
import {ClassroomBody} from "../domain/dto/ClassroomDtoProperties";
import {ErrorAlert} from "./error/ErrorAlert";
import {alignHorizontalyBoxStyle, homeBoxStyle, typographyStyle} from "../utils/Style";

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
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h2"
                        gutterBottom
                        sx={typographyStyle}
                    >
                        {content.properties.name}
                    </Typography>
                    <Typography
                        variant="subtitle2"
                        gutterBottom
                        sx={typographyStyle}
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
                    { user == AuthState.Teacher ? (
                        <Box sx={alignHorizontalyBoxStyle}>
                            { !content.properties.isArchived ? (
                                <>
                                    <Button variant="contained" onClick={handleCreateAssigment}>Create Assignment</Button>
                                    <Button variant="contained" onClick={handleArchiveClassroom}>Archive Classroom</Button>
                                </>
                            ):null}
                            <Button variant="contained" onClick={handleLocalCopy}>Local Copy</Button>
                        </Box>
                    ):null}
                    <Grid
                        container
                        spacing={1}
                        direction="row"
                        justifyContent="center"
                        alignItems="center"
                    >
                        <Grid item xs={7} md={2}>
                            <List
                                subheader={
                                    <ListSubheader
                                        sx={{
                                            borderRadius: 1
                                        }}
                                    >
                                        <Typography
                                            variant="h6"
                                            gutterBottom
                                            sx={typographyStyle}
                                        >
                                            Assignments
                                        </Typography>
                                    </ListSubheader>
                                }
                                sx={{
                                    maxHeight: 500,
                                    position: 'relative',
                                    overflow: 'auto',
                                    justifyContent:"center",
                                    alignItems:"center",
                                    flexDirection:"column",
                                    "&::-webkit-scrollbar": {
                                        width: 5,
                                    },
                                    "&::-webkit-scrollbar-track": {
                                        boxShadow: `inset 0 0 6px rgba(0, 0, 0, 0.3)`,
                                    },
                                    "&::-webkit-scrollbar-thumb": {
                                        backgroundColor: "darkgrey",
                                        outline: `1px solid slategrey`,
                                    },
                                }}
                            >
                                {content.properties.assignments.map( assigment => (
                                    <ListItem key={assigment.id}>
                                        <Link to={"/courses/" + courseId  + "/classrooms/" + assigment.classroomId  + "/assignments/" + assigment.id}>
                                            {assigment.title + " - " + assigment.description}
                                        </Link>
                                    </ListItem>
                                ))}
                            </List>
                        </Grid>
                        <Grid item xs={7} md={2} >
                        <List
                            subheader={
                                <ListSubheader
                                    sx={{
                                        borderRadius: 1,

                                    }}
                                >
                                    <Typography
                                        variant="h6"
                                        gutterBottom
                                        sx={typographyStyle}
                                    >
                                        Students
                                    </Typography>
                                </ListSubheader>
                            }
                            sx={{
                                maxHeight: 500,
                                position: 'relative',
                                overflow: 'auto',
                                justifyContent:"center",
                                alignItems:"center",
                                flexDirection:"column",
                                "&::-webkit-scrollbar": {
                                    width: 5,
                                },
                                "&::-webkit-scrollbar-track": {
                                    boxShadow: `inset 0 0 6px rgba(0, 0, 0, 0.3)`,
                                },
                                "&::-webkit-scrollbar-thumb": {
                                    backgroundColor: "darkgrey",
                                    outline: `1px solid slategrey`,
                                },
                            }}
                        >
                            {content.properties.students.map( student => (
                                <ListItem key={student.id}>
                                    {student.name + " - " + student.schoolId}
                                </ListItem>
                            ))}
                        </List>
                        </Grid>
                    </Grid>
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </Box>
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
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            navigate("/courses/" + courseId + "/classrooms/" + result.properties.id);
        }
    }, [name,setError,navigate]);


    return (
        <Box sx={homeBoxStyle}>
            <TextField
                label="Classroom Name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                sx={{
                    margin:"8px",
                }}
            />
            <Button variant="contained" onClick={handleCreateClassroom}>Create Classroom</Button>
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </Box>
    )
}
