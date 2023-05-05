import * as React from "react"
import { useAsync } from "../../http/Fetch"
import { ErrorMessageModel } from "../../domain/response-models/Error"
import { SirenEntity } from "../../http/Siren"
import {Backdrop, Box, CircularProgress, List, Typography} from "@mui/material"
import { AuthServices } from "../../services/AuthServices"
import { Error } from "../error/Error"
import {homeBoxStyle, typographyStyle} from "../../utils/Style";

export function ShowStatusFetch({
    authServices,
}: {
    authServices: AuthServices
}) {

     const content = useAsync(async () => {
        return await authServices.status()
    })

    if (window.opener) {
        window.opener.postMessage({type:"Auth", data:'/auth/status'},process.env.NGROK_URI)
        window.close()
    }

    if (!content) {
        return (
            <Backdrop
                sx={{ color: 'primary', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                open={true}
            >
                <CircularProgress color="primary" />
            </Backdrop>
        )
    }

    if (content instanceof ErrorMessageModel) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
    }



    return (
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h2"
                        sx={typographyStyle}
                    >
                        {"Status"}
                    </Typography>
                    <Typography
                        variant="h5"
                        sx={typographyStyle}
                    >
                                {content.properties.statusInfo}
                    </Typography>
                    <Typography
                        variant="subtitle1"
                        sx={typographyStyle}
                    >
                        {content.properties.message}
                    </Typography>
                </>
            ) : null}
        </Box>
    )
}
