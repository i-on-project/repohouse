import * as React from "react";
import {useCallback, useState} from "react";
import {useAsync} from "../http/Fetch";
import {ErrorMessageModel} from "../domain/response-models/Error";
import {SirenEntity} from "../http/Siren";
import {Backdrop, Box, Button, CircularProgress, Grid, List, ListItem, TextField, Typography} from "@mui/material";
import {Link, useNavigate} from "react-router-dom";
import {ErrorAlert} from "./error/ErrorAlert";
import {AuthState, useLoggedIn} from "./auth/Auth";
import {DeliveryServices} from "../services/DeliveryServices";
import {DeliveryBody, DeliveryDtoProperties} from "../domain/dto/DeliveryDtoProperties";
import {DeliveryDomain} from "../domain/Delivery";
import {alignHorizontalyBoxStyle, homeBoxStyle, typographyStyle} from "../utils/Style";

export function ShowDeliveryFetch({
                                  deliveryServices,courseId,classroomId,assignmentId,deliveryId
                              }: {
    deliveryServices: DeliveryServices;
    courseId: number;
    classroomId: number;
    assignmentId: number;
    deliveryId: number;
}) {
    const content = useAsync(async () => {
        return await deliveryServices.delivery(courseId, classroomId, assignmentId, deliveryId);
    });
    
    const [error, setError] = useState<ErrorMessageModel>(null);
    const navigate = useNavigate();
    const user = useLoggedIn()

    const handleSyncDelivery = useCallback(async () => {
        const result = await deliveryServices.syncDelivery(courseId,classroomId,assignmentId,deliveryId);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            // TODO
        }
    }, [setError]);

    const handleDeleteDelivery = useCallback(async () => {
        const result = await deliveryServices.deleteDelivery(courseId,classroomId,assignmentId,deliveryId);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            if (result.properties.deleted) {
                navigate(`/courses/${courseId}/classrooms/${classroomId}/assignments/${assignmentId}`, {replace: true})
            }
        }
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
                         sx={typographyStyle}
                   >
                       {"Delivery # " + content.properties.delivery.id}
                    </Typography>
                    <Typography
                        variant="h4"
                        sx={typographyStyle}
                    >
                        {content.properties.delivery.tagControl + " -  " + new Date(content.properties.delivery.dueDate).toLocaleString(
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
                    {user == AuthState.Teacher ? (
                        <Box sx={alignHorizontalyBoxStyle}>
                            <Button variant="contained" onClick={handleSyncDelivery}>Sync</Button>
                            <Button variant="contained" onClick={() => navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +  "/deliveries/" + content.properties.delivery.id + "/edit",{state:content.properties.delivery})}> Edit </Button>
                            {content.properties.teamsDelivered.length == 0 && content.properties.teamsNotDelivered.length == 0 ? (
                                <Button variant="contained" onClick={handleDeleteDelivery}>Delete</Button>
                            ) : null}
                        </Box>
                    ): null}
                    <Typography
                        variant="subtitle1"
                        sx={typographyStyle}
                    >
                        {"Last Sync Time: " + new Date(content.properties.delivery.lastSync).toLocaleString(
                            "en-GB",
                            {
                                hour: "2-digit",
                                minute: "2-digit",
                                month: "short",
                                day: "2-digit",
                            }
                        )}
                    </Typography>
                    <Grid
                        container
                        direction="row"
                        justifyContent="center"
                        alignItems="center"
                    >
                        <Grid item xs={4}>
                            <Typography
                                variant="h4"
                                sx={typographyStyle}
                            >
                                {"Teams delivered"}
                            </Typography>
                            <List
                                sx={{ width: '100%', maxWidth: 360}}
                            >
                                {content.properties.teamsDelivered.map((team) => (
                                    <ListItem>
                                        <Typography
                                            variant="h6"
                                        >
                                            <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/teams/" + team.id}>
                                                {team.name}
                                            </Link>
                                        </Typography>
                                    </ListItem>
                                ))}
                            </List>
                        </Grid>
                        <Grid item xs={4}>
                            <Typography
                                variant="h4"
                                sx={typographyStyle}
                            >
                                {"Teams not delivered"}
                            </Typography>
                            <List
                                sx={{ width: '100%', maxWidth: 360}}
                            >
                                {content.properties.teamsNotDelivered.map((team) => (
                                    <ListItem>
                                        <Typography
                                            variant="h6"
                                        >
                                            <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/teams/" + team.id}>
                                                {team.name}
                                            </Link>
                                        </Typography>
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

export function ShowCreateDelivery({ deliveryServices,courseId,classroomId,assignmentId, error }: { deliveryServices: DeliveryServices, courseId:number,classroomId:number,assignmentId:number,error: ErrorMessageModel }) {
    const [serror, setError] = useState<ErrorMessageModel>(error);
    const [tagControl, setTagControl] = useState<string>("");
    const [dueDate, setDueDate] = useState<string>("");
    const navigate = useNavigate();

    const handleCreateDelivery = useCallback(async () => {
        const body = new DeliveryBody(tagControl, dueDate, assignmentId)
        const result = await deliveryServices.createDelivery(courseId, classroomId,assignmentId, body);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +  "/deliveries/" + result.properties.delivery.id);
        }
    }, [deliveryServices, tagControl, dueDate, assignmentId, navigate]);

    return (
        <Box sx={homeBoxStyle}>
            <Typography
                variant="h2"
                sx={typographyStyle}
            >
                Create Delivery
            </Typography>
            <TextField
                label="Tag Control"
                value={tagControl}
                onChange={(e) => setTagControl(e.target.value)}
                sx={{margin:"4px"}}
            />
            <TextField
                hiddenLabel
                value={dueDate}
                type={"date"}
                inputProps={{ min: new Date().toISOString().split("T")[0] }}
                onChange={(e) => setDueDate(e.target.value)}
                sx={{margin:"4px"}}
            />
            <Button variant="contained" onClick={handleCreateDelivery}>Create</Button>
            <ErrorAlert error={serror} onClose={() => setError(null)}/>
        </Box>
    );
}

export function ShowEditDelivery({ deliveryServices, delivery,courseId,classroomId,assignmentId, error }: { deliveryServices: DeliveryServices, delivery: DeliveryDomain, courseId:number,classroomId:number,assignmentId:number,error: ErrorMessageModel }) {
    const [serror, setError] = useState<ErrorMessageModel>(error);
    const [tagControl, setTagControl] = useState<string>(delivery.tagControl);
    const [dueDate, setDueDate] = useState<string>(String(delivery.dueDate).split("T")[0])
    const navigate = useNavigate();

    const handleEditDelivery = useCallback(async () => {
        const body = new DeliveryBody(tagControl, dueDate, assignmentId)
        const result = await deliveryServices.editDelivery(courseId, classroomId,assignmentId, delivery.id, body)
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +  "/deliveries/" + result.properties.delivery.id);
        }
    }, [deliveryServices, tagControl, dueDate, navigate]);

    return (
        <Box sx={homeBoxStyle}>
            <Typography
                variant="h2"
                sx={typographyStyle}
            >
                Edit Delivery
            </Typography>
            <TextField
                label="Tag Control"
                value={tagControl}
                onChange={(e) => setTagControl(e.target.value)}
                sx={{margin:"4px"}}
            />
            <TextField
                hiddenLabel
                value={dueDate}
                type={"date"}
                inputProps={{ min: new Date().toISOString().split("T")[0] }}
                onChange={(e) => setDueDate(e.target.value)}
                sx={{margin:"4px"}}
            />
            <Button variant="contained"  onClick={handleEditDelivery}>Edit</Button>
            <ErrorAlert error={serror} onClose={() => setError(null)}/>
        </Box>
    );
}