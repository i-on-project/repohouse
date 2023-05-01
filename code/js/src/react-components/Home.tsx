import * as React from "react"
import { useAsync } from "../http/Fetch"
import { SirenEntity } from "../http/Siren"
import { SystemServices } from "../services/SystemServices"
import { Typography } from "@mui/material"
import { Navigate } from "react-router-dom"
import { AuthState, useLoggedIn } from "./auth/Auth"
import { AuthServices } from "../services/AuthServices"
import { useSetLogin } from "./auth/Auth"


export function ShowHomeFetch({
    authServices,
    systemServices,
}: {
    authServices: AuthServices
    systemServices: SystemServices
}) {

    const loggedin = useLoggedIn()
    const setLoggedIn = useSetLogin()
    const home = useAsync(async () => {
        const home = await systemServices.home()
        const state = await authServices.state()
        if (state instanceof SirenEntity && state.properties.authenticated) {
            if (state.properties.user === "Student") setLoggedIn(AuthState.Student) 
            if (state.properties.user === "Teacher") setLoggedIn(AuthState.Teacher) 
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
        )
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
        </div>
    )
}
