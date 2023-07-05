import {useAsync} from "../http/Fetch";
import {TeamServices} from "../services/TeamServices";
import {ErrorMessageModel} from "../domain/response-models/Error";
import * as React from "react";
import {useCallback, useState,useEffect} from "react";
import {
    Backdrop,
    Box,
    Button,
    CircularProgress,
    Dialog,
    DialogActions,
    DialogContent,
    DialogProps,
    DialogTitle,
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
import {LeaveTeamBody} from "../domain/dto/RequestDtoProperties";
import {FeedbackBody, TeamDtoProperties} from "../domain/dto/TeamDtoProperties";
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
    const [team, setTeam] = useState<TeamDtoProperties | null>(null);
    const [refresh,setRefresh] = useState<boolean>(true);
    const [serror, setError] = useState<ErrorMessageModel>(error);
    const [label, setLabel] = useState<string>("");
    const [description, setDescription] = useState<string>("");
    const [open, setOpen] = React.useState(true);
    const [fullWidth, setFullWidth] = React.useState(true);
    const [maxWidth, setMaxWidth] = React.useState<DialogProps['maxWidth']>('sm');
    const navigate = useNavigate();
    const user = useLoggedIn()
    const userId = useUserId()

    const refreshing = useCallback(async () => {
        if (refresh) {
            if (error) return
            const content = await teamServices.team(courseId, classroomId, assignmentId, teamId);
            if (content instanceof SirenEntity) {
                setTeam(content.properties)
            }
            if (content instanceof ErrorMessageModel) {
                setError(content)
            }
            setRefresh(false)
        }
    }, [error,refresh,setTeam])

    React.useEffect(() => {
        if (refresh) {
            refreshing()
        }
    }, [refresh])

    const handleClickOpen = useCallback(() => {
        setOpen(true);
    }, [setOpen]);

    const handleClose = useCallback(() => {
        setOpen(false);
    }, [setOpen]);

    const handleLeaveTeam = useCallback(async () => {
        const body = new LeaveTeamBody(teamId, null)
        const result = await teamServices.leaveTeam(body,courseId,classroomId,assignmentId,teamId);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
    }, [setError]);

    const handleCloseTeam = useCallback(async () => {
        const result = await teamServices.closeTeam(courseId,classroomId,assignmentId,teamId);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            setRefresh(true)
        }
    }, [setError,setRefresh]);

    const handleSendFeedback = useCallback(async () => {
        if (label === "" || description === "") return
        const body = new FeedbackBody(label, description, teamId)
        const result = await teamServices.sendFeedback(courseId,classroomId,assignmentId,teamId,body)
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            setRefresh(true)
        }
    }, [setError, label, description,navigate]);

    if (!team) {
        return (
            <Backdrop
                sx={{ color: 'primary', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                open={true}
            >
                <CircularProgress color="primary" />
            </Backdrop>
        );
    }

    return (
        <Box sx={homeBoxStyle}>
            {team ? (
                <>
                    {team.team.isCreated == false ? (
                        <Dialog
                            fullWidth={fullWidth}
                            maxWidth={maxWidth}
                            open={open}
                            onClose={handleClose}
                            sx={{zIndex: (theme) => theme.zIndex.drawer + 1}}
                        >
                            <DialogTitle>
                                <Typography
                                    variant="h5"
                                    sx={typographyStyle}
                                >
                                    Team Being Created
                                </Typography>
                            </DialogTitle>
                            <DialogContent>
                                <Typography
                                    variant="h6"
                                    sx={typographyStyle}
                                >
                                    {"There is a request to create this team. Please wait for the teacher to accept it."}
                                </Typography>
                            </DialogContent>
                            <DialogActions>
                                <Button onClick={handleClose}>Close</Button>
                            </DialogActions>
                        </Dialog>
                    ): null}
                    <Typography
                        variant="h2"
                        sx={typographyStyle}
                    >
                        {"Team " + team.team.name}
                    </Typography>
                    {team.repo == null ?
                        <Typography
                            variant="h5"
                            sx={typographyStyle}
                        >
                            No repo available - ensure you close the team
                        </Typography>
                    :
                        <List>
                            <ListItem>
                                <a href={team.repo.url} target="_blank" rel="noreferrer">
                                    {team.repo.name}
                                </a>
                            </ListItem>
                        </List>
                    }
                    <Box sx={alignHorizontalyBoxStyle}>
                        <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +"/teams/" + team.team.id + "/requests"}> Requests History </Link>
                        {user === AuthState.Student ?(
                            <>
                                { team.students.find(student => student.id === userId) ? (
                                    <>
                                        <Button onClick={handleLeaveTeam}>Leave Team</Button>
                                        { team.team.isClosed == false  && team.students.length >= team.assignment.minElemsPerGroup ? (
                                            <Button onClick={handleCloseTeam}>Close Team</Button>
                                        ) : null}
                                    </>
                                ) : null}
                            </>
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
                                {team.students.map((student) =>
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
                                {team.feedbacks.map((feedback) =>
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
                                    <MenuItem  value="General">Info</MenuItem >
                                    <MenuItem  value="Notice">Test1</MenuItem >
                                    <MenuItem  value="Task">Amor</MenuItem >
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
    teamServices,courseId,classroomId,assignmentId,teamId,serror
}: {
    teamServices: TeamServices,
    courseId: number,
    classroomId: number,
    assignmentId: number,
    teamId: number,
    serror: ErrorMessageModel
}) {
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState<ErrorMessageModel>(serror);
    const navigate = useNavigate();
    const user = useLoggedIn()

    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const res = await teamServices.teamRequests(courseId,classroomId,assignmentId,teamId);
            if (!cancelled) {
                setContent(res)
            }
        }
        doFetch()
        return () => {
            cancelled = true
        }
    }, [])

    const handleChangeStatus = useCallback(async (requestId:number) => {
        const result = await teamServices.changeRequestStatus(courseId,classroomId,assignmentId,teamId,requestId);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            const update = await teamServices.teamRequests(courseId,classroomId,assignmentId,teamId);
            setContent(update)
        }
    }, [setError,setContent]);

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
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </Box>
    );
}


