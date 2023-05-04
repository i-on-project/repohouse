import * as React from "react"
import { useAsync } from "../../http/Fetch"
import { ErrorMessageModel } from "../../domain/response-models/Error"
import { SirenEntity } from "../../http/Siren"
import {Backdrop, CircularProgress, Typography} from "@mui/material"
import { AuthServices } from "../../services/AuthServices"
import { Error } from "../error/Error"

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
    )
}
