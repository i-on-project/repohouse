import * as React from "react"
import { useCallback } from "react"
import { ErrorMessageModel } from "../../domain/response-models/Error"
import { SirenEntity } from "../../http/Siren"
import { Typography } from "@mui/material"
import { Button } from "react-bootstrap"
import { Navigate, useNavigate } from "react-router-dom"
import { AuthServices } from "../../services/AuthServices"
import { Error } from "../error/Error"
import { useAsync } from "../../http/Fetch"


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
            <Typography
                variant="h6"
                gutterBottom
            >
                ...loading...
            </Typography>
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
                        {content.properties.name}
                    </Typography>
                    <Typography
                        variant="h5"
                    >
                        {content.properties.email}
                    </Typography>
                    <Typography
                        variant="h6"
                    >
                        {content.properties.GitHubUsername}
                    </Typography>
                    <Button onClick={handleConfirmClick}> {"Confirm"} </Button>
                    <Button onClick={handleDeclineClick}> {"Decline"} </Button>
                </>
            ) : null}
        </div>
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
            <Typography
                variant="h6"
                gutterBottom
            >
                ...loading...
            </Typography>
        );
    }

    if (content instanceof ErrorMessageModel) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
    }

    return <Navigate to={"/auth/status"}/>
}

export function ShowCreateCallbackTeacher() {
    window.opener.postMessage({type:"Auth", data:'/auth/create/teacher'},'https://324b-2001-818-e975-8500-174-d17d-e3f5-574f.ngrok-free.app')
    window.close()
    return (<> </>)
}
