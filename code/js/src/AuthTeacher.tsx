import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import {Typography} from "@mui/material";
import {AuthServices} from "./services/AuthServices";

export function ShowAuthTeacherFetch({
                                         authServices,
                                     }: {
    authServices: AuthServices;
}) {
    const content = useAsync(async () => {
        return await authServices.authTeacher();
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
                window.location.href = content.properties.url
            ) : null}
        </div>
    );
}