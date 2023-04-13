import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useCallback, useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import {Typography} from "@mui/material";
import {Button} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import {AuthServices} from "./services/AuthServices";
import {useCookies} from "react-cookie";

export function ShowAuthCallbackFetch({
                                         authServices,
                                     }: {
    authServices: AuthServices;
}) {

    const content = useAsync(async () => {
        return await authServices.authCallback();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);

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
                window.location.href = content.properties.statusInfo
            ) : null}
        </div>
    );
}