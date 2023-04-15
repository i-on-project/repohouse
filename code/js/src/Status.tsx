import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import { Typography } from "@mui/material";
import {AuthServices} from "./services/AuthServices";

export function ShowStatusFetch({
    authServices,
}: {
    authServices: AuthServices;
}) {
    const content = useAsync(async () => {
        return await authServices.status();
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
                <>
                    <Typography
                        variant="h2"
                    >
                        {"Status"}
                    </Typography>
                    <Typography
                        variant="h5"
                    >
                        <ul>
                            <li>
                                {content.properties.statusInfo}
                            </li>
                            <li>
                                {content.properties.message}
                            </li>
                        </ul>
                    </Typography>
                </>
            ) : null}
        </div>
    );
}


export function ShowStatusCallbackFetch() {
    window.opener.postMessage({type:"Auth", data:'http://localhost:3000/auth/status'},'http://localhost:3000/')
    window.close()
    return (<> </>)
}