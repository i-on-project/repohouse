import * as React from "react"
import { useAsync } from "../http/Fetch"
import { SirenEntity } from "../http/Siren"
import { SystemServices } from "../services/SystemServices"
import { Typography } from "@mui/material"
import { Navigate } from "react-router-dom"
import { useLoggedIn } from "./auth/Auth"
import { LoadingAnimation } from "./animation/Loading"


export function ShowHomeFetch({
    systemServices,
}: {
    systemServices: SystemServices
}) {

    const loggedin = useLoggedIn()
    const home = useAsync(async () => {
        if (!loggedin) return await systemServices.home()
    })
    
    if(loggedin) {
        return <Navigate to="/menu"/>
    }

    if (!home) {
        return <LoadingAnimation/>
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
