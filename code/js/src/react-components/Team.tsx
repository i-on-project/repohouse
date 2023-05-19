import {useAsync} from "../http/Fetch";
import {TeamServices} from "../services/TeamServices";
import {ErrorMessageModel} from "../domain/response-models/Error";
import * as React from "react";
import {useCallback, useState} from "react";
import {
    Backdrop,
    Box,
    CircularProgress,
    Grid,
    List,
    ListItem,
    MenuItem,
    Select,
    TextField,
    Typography
} from "@mui/material";
import {SirenEntity} from "../http/Siren";
import {ErrorAlert} from "./error/ErrorAlert";
import {AuthState, useLoggedIn, useUserId} from "./auth/Auth";
import {Link, useNavigate} from "react-router-dom";
import {Button} from "react-bootstrap";
import {LeaveTeamBody} from "../domain/dto/RequestDtoProperties";
import {FeedbackBody} from "../domain/dto/TeamDtoProperties";
import {alignHorizontalyBoxStyle, homeBoxStyle, typographyStyle} from "../utils/Style";

export function ShowTeamFetch({
    teamServices,courseId,classroomId,assignmentId,teamId,error
}: {
    teamServices: TeamServices,
    courseId: number,
    classroomId: number,
    assignmentId: number,
    teamId: number,
    error: ErrorMessageModel
}) {

    const content = useAsync(async () => {
        return await teamServices.team(courseId,classroomId,assignmentId,teamId);
    });
    const [serror, setError] = useState<ErrorMessageModel>(error);
    const [label, setLabel] = useState<string>("");
    const [description, setDescription] = useState<string>("");
    const navigate = useNavigate();
    const user = useLoggedIn()
    const userId = useUserId()

    const handleLeaveTeam = useCallback(async () => {
        const body = new LeaveTeamBody(teamId, null)
        const result = await teamServices.leaveTeam(body,courseId,classroomId,assignmentId,teamId);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
    }, [setError]);

    const handleSendFeedback = useCallback(async () => {
        if (label === "" || description === "") return
        const body = new FeedbackBody(label, description, teamId)
        const result = await teamServices.sendFeedback(courseId,classroomId,assignmentId,teamId,body)
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            // TODO: Redirect to the same page
            navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +"/teams/" + teamId)
        }
    }, [setError, label, description,navigate]);

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

    if (content instanceof ErrorMessageModel) {
        setError(content);
    }

    return (
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h2"
                        sx={typographyStyle}
                    >
                        {"Team " + content.properties.team.name}
                    </Typography>
                    {content.properties.repo == null ?
                        <Typography
                            variant="h5"
                            sx={typographyStyle}
                        >
                            No repo available
                        </Typography>
                    :
                        <List>
                            <ListItem>
                                <a href={content.properties.repo.url} target="_blank" rel="noreferrer">
                                    {content.properties.repo.name}
                                </a>
                            </ListItem>
                        </List>
                    }
                    <Box sx={alignHorizontalyBoxStyle}>
                        <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +"/teams/" + content.properties.team.id + "/requests"}> Requests History </Link>
                        {user === AuthState.Student  && content.properties.students.find(student => student.id === userId) ? (
                            <Button onClick={handleLeaveTeam}>Leave Team</Button>
                        ): null}
                    </Box>
                    <Grid
                        container
                        direction="row"
                        justifyContent="center"
                        alignItems="center"
                    >
                        <Grid item xs={10} md={5}>
                            <List>
                                {content.properties.students.map((student) =>
                                    <ListItem>
                                        <Typography
                                            variant="h5"
                                            sx={typographyStyle}
                                        >
                                            {student.name + " - " + student.schoolId}
                                        </Typography>
                                    </ListItem>
                                )}
                            </List>
                        </Grid>
                        <Grid item xs={10} md={5}>
                            <List
                               sx={{
                                    borderRadius: 1,
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
                                    }
                               }}
                            >
                                {content.properties.feedbacks.map((feedback) =>
                                    <ListItem>
                                        <Typography
                                            variant="h5"
                                            sx={typographyStyle}
                                        >
                                            {feedback.label + " - " + feedback.description}
                                        </Typography>
                                    </ListItem>
                                )}
                            </List>
                        </Grid>
                    </Grid>
                    {user === AuthState.Teacher ? (
                        <Box
                            sx={{
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                                justifyContent: 'center',
                            }}
                        >
                            <Box sx={alignHorizontalyBoxStyle}>
                                <Select
                                    id="label"
                                    label="Label"
                                    onChange={(event) => setLabel(event.target.value as string)}
                                >
                                    <MenuItem  value="info">Info</MenuItem >
                                    <MenuItem  value="test1">Test1</MenuItem >
                                    <MenuItem  value="amor">Amor</MenuItem >
                                </Select>
                                <TextField
                                    id="description"
                                    label="Description"
                                    multiline
                                    maxRows={4}
                                    onChange={(event) => setDescription(event.target.value)}
                                />
                            </Box>
                            <Button onClick={handleSendFeedback}>Send Feedback</Button>
                        </Box>
                    ): null}
                </>
            ) : null}
            <ErrorAlert error={serror} onClose={() => setError(null)}/>
        </Box>
    );
}

export function ShowTeamRequestsFetch({
    teamServices,courseId,classroomId,assignmentId,teamId,error
}: {
    teamServices: TeamServices,
    courseId: number,
    classroomId: number,
    assignmentId: number,
    teamId: number,
    error: ErrorMessageModel
}) {
    const content = useAsync(async () => {
        return await teamServices.teamRequests(courseId,classroomId,assignmentId,teamId);
    });
    const [serror, setError] = useState<ErrorMessageModel>(error);
    const navigate = useNavigate();
    const user = useLoggedIn()

    const handleChangeStatus = useCallback(async (requestId:number) => {
        const result = await teamServices.changeRequestStatus(courseId,classroomId,assignmentId,teamId,requestId);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +"/teams/" + teamId + "/requests", {replace: true})
        }
    }, [setError,navigate]);

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

    if (content instanceof ErrorMessageModel) {
        setError(content);
    }

    return (
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h2"
                        sx={typographyStyle}
                    >
                        {"Team Requests"}
                    </Typography>
                    <Grid
                        container
                        direction="row"
                        justifyContent="center"
                        alignItems="center"
                    >
                        <Grid item xs={10} md={5}>
                            <Typography
                                variant="h5"
                                sx={typographyStyle}
                            >
                                Join Team Requests
                            </Typography>
                            <List>
                                {content.properties.joinTeam.map((request) =>
                                    <ListItem>
                                        {request.creator + " - " + request.state}
                                        {user === AuthState.Teacher && request.state == "Rejected" ? (
                                            <Button onClick={() => handleChangeStatus(request.id)}>To pending</Button>
                                        ):null}
                                    </ListItem>
                                )}
                            </List>
                        </Grid>
                        <Grid item xs={10} md={5}>
                            <Typography
                                variant="h5"
                                sx={typographyStyle}
                            >
                                Leave Team Requests
                            </Typography>
                            <List>
                                {content.properties.leaveTeam.map((request) =>
                                    <ListItem>
                                        {"User " + request.creator + " - " + request.state}
                                        {user === AuthState.Teacher && request.state == "Rejected" ? (
                                            <Button onClick={() => handleChangeStatus(request.id)}>To pending</Button>
                                        ):null}
                                    </ListItem>

                                )}
                            </List>
                        </Grid>
                    </Grid>
                </>
            ) : null}
            <ErrorAlert error={serror} onClose={() => setError(null)}/>
        </Box>
    );
}


