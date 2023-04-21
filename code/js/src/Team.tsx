import {useAsync} from "./siren/Fetch";
import {TeamServices} from "./services/TeamServices";
import {ErrorMessageModel} from "./domain/response-models/Error";
import * as React from "react";
import {useCallback, useState} from "react";
import {List, ListItem, MenuItem, Select, TextField, Typography} from "@mui/material";
import {SirenEntity} from "./siren/Siren";
import {ErrorAlert} from "./ErrorAlert";
import {AuthState, useLoggedIn} from "./Auth";
import {Link} from "react-router-dom";
import {Button} from "react-bootstrap";
import {LeaveTeamBody} from "./domain/dto/RequestDtoProperties";
import {FeedbackBody} from "./domain/dto/TeamDtoProperties";

export function ShowTeamFetch({
    teamServices,
}: {
  teamServices: TeamServices,
}) {

    const content = useAsync(async () => {
        return await teamServices.team();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const [label, setLabel] = useState<string>("");
    const [description, setDescription] = useState<string>("");
    const user = useLoggedIn()



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

    const handleLeaveTeam = useCallback(async () => {
        const body = new LeaveTeamBody(0, 0) // TODO: fill in the body
        const result = await teamServices.leaveTeam(body);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
    }, [setError]);

    const handleSendFeedback = useCallback(async () => {
        if (label === "" || description === "") return
        const body = new FeedbackBody(label, description, 0) // TODO: fill in the body
        const result = await teamServices.sendFeedback(body);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
    }, [setError, label, description]);


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
                    <Link to={"/team/" + content.properties.team.id + "/requests"}> Requests History </Link>
                    {user === AuthState.Student ? (
                        <>
                            <Button onClick={handleLeaveTeam}>Leave Team</Button>
                        </>
                    ): null}
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </div>
    );
}

export function ShowTeamRequestsFetch({
    teamServices,
}: {
  teamServices: TeamServices,
}) {

    const content = useAsync(async () => {
        return await teamServices.teamRequests();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const user = useLoggedIn()

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

    const handleChangeStatus = useCallback(async () => {
        const result = await teamServices.changeRequestStatus();
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
    }, [setError]);

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
                                {request.creator + " - " + request.state}
                                {user === AuthState.Teacher && request.state == "rejected" ? (
                                    <Button onClick={handleChangeStatus}>To pending</Button>
                                ):null}
                            </ListItem>

                        )}
                    </List>
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </div>
    );
}
