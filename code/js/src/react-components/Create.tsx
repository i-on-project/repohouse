import * as React from "react"
import { useAsync } from "../http/Fetch"
import { useCallback, useState } from "react"
import { ErrorMessageModel } from "../domain/response-models/Error"
import { SirenEntity } from "../http/Siren"
import { TextField, Typography } from "@mui/material"
import { Button } from "react-bootstrap"
import { Navigate, useLocation, useNavigate } from "react-router-dom"
import { AuthServices } from "../services/AuthServices"
import { Error } from "./error/Error"


export function ShowCreateTeacherFetch({
    authServices
}: {
    authServices: AuthServices;
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

export function ShowCreateStudentFetch({
    authServices
}: {
    authServices: AuthServices;
}) {
    const content = useAsync(async () => {
        return await authServices.getRegisterInfo()
    })
    const navigate = useNavigate()
    const [schoolId, setSchoolId] = useState<number>(null)

    const handleConfirmClick = useCallback((event:any) => {
        event.preventDefault()
        navigate("/auth/register/student", {state: {schoolId: schoolId} })
    }, [navigate,schoolId])

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
                    <TextField label={"SchoolId"} required type={"number"} onChange={(event) => setSchoolId(Number(event.target.value))}/>
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


export function ShowCreateStudentFetchPost({
    authServices,
}: {
    authServices: AuthServices
}) {
    const location = useLocation()
    const content = useAsync(async () => {
        return await authServices.createStudentPost(location.state.schoolId);
    })
    
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

    return <Navigate to={"/auth/verify"}/>
}

export function ShowCreateCallbackStudent() {
    window.opener.postMessage({type:"Auth", data:'/auth/create/student'},'http://localhost:3000/')
    window.close()
    return (<> </>)
}

export function ShowCreateCallbackTeacher() {
    window.opener.postMessage({type:"Auth", data:'/auth/create/teacher'},'http://localhost:3000/')
    window.close()
    return (<> </>)
}
