import * as React from "react";
import { useAsync } from "../../http/Fetch";
import {useState} from "react";
import { ErrorMessageModel } from "../../domain/response-models/Error";
import { SirenEntity } from "../../http/Siren";
import { Typography } from "@mui/material";
import {AuthServices} from "../../services/AuthServices";
import {ErrorAlert} from "../error/ErrorAlert";

export function ShowStatusFetch({
    authServices,
}: {
    authServices: AuthServices;
}) {
    if (window.opener) {
        window.opener.postMessage({type:"Auth", data:'/auth/status'},process.env.FRONTEND_NGROK_KEY)
        window.close()
    }

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
            <ErrorAlert error={error} onClose={() => { setError(null) }}/>
        </div>
    );
}


export function ShowStatusCallbackFetch() {
    window.opener.postMessage({type:"Auth", data:'/auth/status'},process.env.FRONTEND_NGROK_KEY)
    window.close()
    return (<> </>)
}