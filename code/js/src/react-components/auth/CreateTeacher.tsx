import * as React from "react"
import { useCallback } from "react"
import { ErrorMessageModel } from "../../domain/response-models/Error"
import { SirenEntity } from "../../http/Siren"
import {Backdrop, Box, CircularProgress, Typography} from "@mui/material"
import { Button } from "react-bootstrap"
import { Navigate, useNavigate } from "react-router-dom"
import { AuthServices } from "../../services/AuthServices"
import { Error } from "../error/Error"
import { useAsync } from "../../http/Fetch"
import {alignHorizontalyBoxStyle, homeBoxStyle, typographyStyle} from "../../utils/Style";


export function ShowCreateTeacherFetch({
    authServices
}: {
    authServices: AuthServices
}) {
    const content = useAsync(async () => {
        return await authServices.getRegisterInfo()
    })
    const navigate = useNavigate()
    
    const handleConfirmClick = useCallback((event:any) => {
        event.preventDefault()
        navigate("/auth/register/teacher")
    }, [navigate])

    const handleDeclineClick = useCallback ((event:any) => {
        event.preventDefault()
        navigate("/")
    }, [navigate])

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
                        {content.properties.name}
                    </Typography>
                    <Typography
                        variant="h5"
                        sx={typographyStyle}
                    >
                        {content.properties.email}
                    </Typography>
                    <Typography
                        variant="h6"
                        sx={typographyStyle}
                    >
                        {content.properties.GitHubUsername}
                    </Typography>
                    <Box sx={alignHorizontalyBoxStyle}>
                        <Button onClick={handleConfirmClick}> {"Confirm"} </Button>
                        <Button onClick={handleDeclineClick}> {"Decline"} </Button>
                    </Box>
                </>
            ) : null}
        </Box>
    )
}

export function ShowCreateTeacherFetchPost({
    authServices,
}: {
    authServices: AuthServices
}) {
    const content = useAsync(async () => {
        return await authServices.createTeacherPost()
    })
   
    if (!content) {
        return (
            <Backdrop
                sx={{ color: 'primary', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                open={true}
            >
                <CircularProgress color="primary" />
            </Backdrop>
        );
    }

    if (content instanceof ErrorMessageModel) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
    }

    return <Navigate to={"/auth/status"}/>
}

export function ShowCreateCallbackTeacher() {
    window.opener.postMessage({type:"Auth", data:'/auth/create/teacher'}, location.origin)
    window.close()
    return (<> </>)
}
