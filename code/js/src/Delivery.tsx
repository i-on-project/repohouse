import * as React from "react";
import {useCallback, useState} from "react";
import {useAsync} from "./siren/Fetch";
import {ErrorMessageModel} from "./domain/response-models/Error";
import {SirenEntity} from "./siren/Siren";
import {CardContent, TextField, Typography} from "@mui/material";
import {Link, useNavigate} from "react-router-dom";
import {Button, Card} from "react-bootstrap";
import {ErrorAlert} from "./ErrorAlert";
import {AuthState, useLoggedIn} from "./Auth";
import {DeliveryServices} from "./services/DeliveryServices";
import {DeliveryBody} from "./domain/dto/DeliveryDtoProperties";
import {DeliveryDomain} from "./domain/Delivery";

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
        return await deliveryServices.delivery(courseId,classroomId,assignmentId,deliveryId);
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
            // TODO: refresh/update content of the delivery page
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
                       {"Delivery # " + content.properties.delivery.id}
                    </Typography>
                    <Typography
                        variant="h4"
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
                    <Typography
                        variant="h4"
                    >
                        {"Last Sync Time: "}
                    </Typography>
                    Teams delivered:
                    {content.properties.teamsDelivered.map((team) => (
                        <Card>
                            <CardContent>
                                <Typography
                                    variant="h6"
                                >
                                    <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/teams/" + team.id}>
                                        {team.name}
                                    </Link>
                                </Typography>
                            </CardContent>
                        </Card>
                    ))}
                    Teams not delivered:
                    {content.properties.teamsNotDelivered.map((team) => (
                        <Card>
                            <CardContent>
                                <Typography
                                    variant="h6"
                                >
                                    <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/teams/" + team.id}>
                                        {team.name}
                                    </Link>
                                </Typography>
                            </CardContent>
                        </Card>
                    ))}
                    {user == AuthState.Teacher ? (
                        <>
                            <Button onClick={handleSyncDelivery}>Sync</Button>
                            <Button onClick={() => navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +  "/deliveries/" + content.properties.delivery.id + "/edit",{state:content.properties.delivery})}> Edit </Button>
                            {content.properties.teamsDelivered.length == 0 && content.properties.teamsNotDelivered.length == 0 ? (
                                <Button onClick={handleDeleteDelivery}>Delete</Button>
                            ) : null}
                        </>
                    ): null}
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </div>
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
        <div
            style={{
                alignItems: "center",
                justifyContent: "space-evenly",
            }}
        >
            <Typography
                variant="h2"
            >
                Create Delivery
            </Typography>
            <TextField
                label="Tag Control"
                value={tagControl}
                onChange={(e) => setTagControl(e.target.value)}
            />
            <TextField
                label="Due Date"
                value={dueDate}
                type={"date"}
                inputProps={{ min: new Date().toISOString().split("T")[0] }}
                onChange={(e) => setDueDate(e.target.value)}
            />
            <Button onClick={handleCreateDelivery}>Create</Button>
            <ErrorAlert error={serror} onClose={() => setError(null)}/>
        </div>
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
        <div
            style={{
                alignItems: "center",
                justifyContent: "space-evenly",
            }}
        >
            <Typography
                variant="h2"
            >
                Edit Delivery
            </Typography>
            <TextField
                label="Tag Control"
                value={tagControl}
                onChange={(e) => setTagControl(e.target.value)}
            />
            <TextField
                label="Due Date"
                value={dueDate}
                type={"date"}
                inputProps={{ min: new Date().toISOString().split("T")[0] }}
                onChange={(e) => setDueDate(e.target.value)}
            />
            <Button onClick={handleEditDelivery}>Edit</Button>
            <ErrorAlert error={serror} onClose={() => setError(null)}/>
        </div>
    );
}