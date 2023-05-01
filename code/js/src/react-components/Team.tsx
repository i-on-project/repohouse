import {useAsync} from "../http/Fetch";
import {TeamServices} from "../services/TeamServices";
import {ErrorMessageModel} from "../domain/response-models/Error";
import * as React from "react";
import {useCallback, useState} from "react";
import {List, ListItem, MenuItem, Select, TextField, Typography} from "@mui/material";
import {SirenEntity} from "../http/Siren";
import {ErrorAlert} from "./error/ErrorAlert";
import {AuthState, useLoggedIn} from "./auth/Auth";
import {Link, useNavigate} from "react-router-dom";
import {Button} from "react-bootstrap";
import {LeaveTeamBody} from "../domain/dto/RequestDtoProperties";
import {FeedbackBody} from "../domain/dto/TeamDtoProperties";

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
            navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +"/teams/" + teamId, {replace: true})
        }
    }, [setError, label, description,navigate]);

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

    if (content instanceof ErrorMessageModel) {
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
                        {"Team " + content.properties.team.name}
                    </Typography>
                    <List>
                        {content.properties.students.map((student) =>
                            <ListItem>
                                {student.name + " - " + student.schoolId}
                            </ListItem>
                        )}
                    </List>
                    <List>
                        {content.properties.repos.map((repo) =>
                            <ListItem>
                                <a href={repo.url} target="_blank" rel="noreferrer">
                                    {repo.name}
                                </a>
                            </ListItem>
                        )}
                    </List>
                    <List>
                        {content.properties.feedbacks.map((feedback) =>
                            <ListItem>
                                {feedback.label + " - " + feedback.description}
                            </ListItem>
                        )}
                    </List>
                    {user === AuthState.Teacher ? (
                        <>
                            <Select
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
                            <Button onClick={handleSendFeedback}>Send Feedback</Button>
                        </>
                    ): null}
                    <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +"/teams/" + content.properties.team.id + "/requests"}> Requests History </Link>
                    {user === AuthState.Student ? (
                        <Button onClick={handleLeaveTeam}>Leave Team</Button>
                    ): null}
                </>
            ) : null}
            <ErrorAlert error={serror} onClose={() => setError(null)}/>
        </div>
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
            <Typography
                variant="h6"
                gutterBottom
            >
                ...loading...
            </Typography>
        );
    }

    if (content instanceof ErrorMessageModel) {
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
                        {"Team Requests"}
                    </Typography>
                    <List>
                        {"Join Team Requests"}
                        {content.properties.joinTeam.map((request) =>
                            <ListItem>
                                {request.creator + " - " + request.state}
                            </ListItem>
                        )}
                    </List>
                    <List>
                        {"Leave Team Requests"}
                        {content.properties.leaveTeam.map((request) =>
                            <ListItem>
                                {"User " + request.creator + " - " + request.state}
                                {user === AuthState.Teacher && request.state == "Rejected" ? (
                                    <Button onClick={() => handleChangeStatus(request.id)}>To pending</Button>
                                ):null}
                            </ListItem>

                        )}
                    </List>
                </>
            ) : null}
            <ErrorAlert error={serror} onClose={() => setError(null)}/>
        </div>
    );
}


