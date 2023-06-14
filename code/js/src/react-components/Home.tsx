import * as React from "react"
import { useAsync } from "../http/Fetch"
import { SirenEntity } from "../http/Siren"
import { SystemServices } from "../services/SystemServices"
import {Backdrop, Box, CircularProgress, Typography} from "@mui/material"
import { Navigate } from "react-router-dom"
import { useLoggedIn } from "./auth/Auth"
import {homeBoxStyle, typographyStyle} from "../utils/Style";


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
        return (
            <Backdrop
                sx={{ color: 'primary', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                open={true}
            >
                <CircularProgress color="primary" />
            </Backdrop>
        )
    }

    return (
        <Box sx={homeBoxStyle}>
            {home instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h1"
                        sx={typographyStyle}
                    >
                        {home.properties.title}
                    </Typography>
                    <Typography
                        variant="h6"
                        sx={typographyStyle}
                    >
                        {home.properties.description}
                    </Typography><Typography
                        variant="h6"
                        sx={typographyStyle}
                    >
                        {home.properties.subDescription}
                    </Typography>
                    <Typography
                        variant="subtitle2"
                        sx={typographyStyle}
                    >
                        {"est: " + home.properties.est}
                    </Typography>
                </>
            ) : null}
        </Box>
    )
}
