import * as React from "react";
import {useState} from "react";
import {useAsync} from "./siren/Fetch";
import {ErrorMessageModel} from "./domain/response-models/Error";
import {SirenEntity} from "./siren/Siren";
import {SystemServices} from "./services/SystemServices";
import {Typography} from "@mui/material";
import {Navigate} from "react-router-dom";
import {ErrorAlert} from "./ErrorAlert";
import { AuthState, useLoggedIn } from "./Auth";
import { AuthServices } from "./services/AuthServices";
import { Student } from "./domain/User";
import { useSetLogin } from "./Auth";

export function ShowHomeFetch({
    authServices,
    systemServices,
}: {
    authServices: AuthServices
    systemServices: SystemServices
}) {

    const loggedin = useLoggedIn()
    const setLoggedIn = useSetLogin()
    const [error, setError] = useState<ErrorMessageModel>(null)
    const home = useAsync(async () => {
        const home = await systemServices.home()
        const state = await authServices.state()
        if (state instanceof SirenEntity && state.properties.authenticated) {
            if (state.properties.user instanceof Student) setLoggedIn(AuthState.Student) 
            else setLoggedIn(AuthState.Teacher)
        }
        return home
    })
    
    if(loggedin) {
        return <Navigate to="/menu"/>
    }

    if (!home) {
        return (
            <Typography
                variant="h6"
                gutterBottom
            >
                ...loading...
            </Typography>
        );
    }

    if (home instanceof ErrorMessageModel && !error) {
        setError(home);
    }

    if (home) {
        if (loggedin) {
            return <Navigate to="/menu"/>
        }
        return 
    }

    return (
        <div
            style={{
                alignItems: "center",
                justifyContent: "space-evenly",
            }}
        >
            {home instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h2"
                    >
                        {home.properties.title}
                    </Typography>
                    <Typography
                        variant="h5"
                    >
                        {home.properties.description}
                    </Typography>
                    <Typography
                        variant="h6"
                    >
                        {"est: "+ home.properties.est}
                    </Typography>
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => { setError(null) }}/>
        </div>
    );
}
