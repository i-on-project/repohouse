import * as React from "react";
import {useCallback, useState} from "react";
import {useAsync} from "./siren/Fetch";
import {ErrorMessageModel} from "./domain/response-models/Error";
import {SirenEntity} from "./siren/Siren";
import {CardActions, CardContent, TextField, Typography} from "@mui/material";
import {Link, useNavigate} from "react-router-dom";
import {Button, Card} from "react-bootstrap";
import {AssignmentServices} from "./services/AssignmentServices";
import {ErrorAlert} from "./ErrorAlert";
import {AuthState, useLoggedIn} from "./Auth";
import {DeliveryServices} from "./services/DeliveryServices";
import {DeliveryBody} from "./domain/dto/DeliveryDtoProperties";
import {DeliveryDomain} from "./domain/Delivery";

export function ShowDeliveryFetch({
                                  deliveryServices
                              }: {
    deliveryServices: DeliveryServices;
}) {
    const content = useAsync(async () => {
        return await deliveryServices.delivery();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const user = useLoggedIn()
    const navigate = useNavigate();

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

    const handleSyncDelivery = useCallback(async () => {
        const result = await deliveryServices.syncDelivery();
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            // TODO: refresh/update content of the delivery page
        }
    }, [deliveryServices]);

    const handleDeleteDelivery = useCallback(async () => {
        const result = await deliveryServices.deleteDelivery();
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            // TODO : navigate to the delivery page if deleted
        }
    }, [deliveryServices]);

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
                        {content.properties.delivery.tagControl + " -  " + content.properties.delivery.dueDate}
                    </Typography>
                    <Typography
                        variant="h4"
                    >
                        {"Last Sync Time: "}
                    </Typography>
                    {content.properties.teamsDelivered.map((team) => (
                        <Card>
                            <CardContent>
                                <Typography
                                    variant="h6"
                                >
                                    <Link to={"/assignments/" + content.properties.delivery.assignmentId + "/teams/" + team.id}>
                                        {team.name}
                                    </Link>
                                </Typography>
                            </CardContent>
                        </Card>
                    ))}
                    {content.properties.teamsNotDelivered.map((team) => (
                        <Card>
                            <CardContent>
                                <Typography
                                    variant="h6"
                                >
                                    <Link to={"/assignments/" + content.properties.delivery.assignmentId + "/teams/" + team.id}>
                                        {team.name}
                                    </Link>
                                </Typography>
                            </CardContent>
                        </Card>
                    ))}
                    {user == AuthState.Teacher ? (
                        <>
                            <Button onClick={handleSyncDelivery}>Sync</Button>
                            <Link to={"/assignments/" + content.properties.delivery.assignmentId + "/deliveries/" + content.properties.delivery.id + "/edit"} state={{delivery:content.properties.delivery}}> Edit </Link>
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

export function ShowCreateDelivery({ deliveryServices,assignmentId, error }: { deliveryServices: DeliveryServices, assignmentId:number,error: ErrorMessageModel }) {
    const [serror, setError] = useState<ErrorMessageModel>(error);
    const [tagControl, setTagControl] = useState<string>("");
    const [dueDate, setDueDate] = useState<string>("");
    const navigate = useNavigate();

    const handleCreateDelivery = useCallback(async () => {
        const body = new DeliveryBody(tagControl, dueDate, assignmentId)
        const result = await deliveryServices.createDelivery(body);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            navigate("/assignments/" + assignmentId + "/deliveries/" + result.properties.delivery.id);
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

export function ShowEditDelivery({ deliveryServices, delivery, error }: { deliveryServices: DeliveryServices, delivery: DeliveryDomain,error: ErrorMessageModel }) {
    const [serror, setError] = useState<ErrorMessageModel>(error);
    const [tagControl, setTagControl] = useState<string>(delivery.tagControl);
    const [dueDate, setDueDate] = useState<string>(delivery.dueDate.toISOString().split("T")[0]);
    const navigate = useNavigate();

    const handleEditDelivery = useCallback(async () => {
        const body = new DeliveryBody(tagControl, dueDate, null)
        const result = await deliveryServices.editDelivery(body);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            navigate("/assignments/" + result.properties.delivery.assignmentId + "/deliveries/" + result.properties.delivery.id);
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