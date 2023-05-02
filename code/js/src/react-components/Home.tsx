import * as React from "react"
import { useAsync } from "../http/Fetch"
import { SirenEntity } from "../http/Siren"
import { SystemServices } from "../services/SystemServices"
import { Typography } from "@mui/material"
import { Navigate } from "react-router-dom"
import { useLoggedIn } from "./auth/Auth"


export function ShowHomeFetch({
    systemServices,
}: {
    systemServices: SystemServices
}) {

    const loggedin = useLoggedIn()
    const home = useAsync(async () => {
        return await systemServices.home()
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