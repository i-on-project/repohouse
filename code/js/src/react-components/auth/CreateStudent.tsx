import * as React from "react"
import { useState, useCallback } from "react"
import { ErrorMessageModel } from "../../domain/response-models/Error"
import { SirenEntity } from "../../http/Siren"
import { Typography, TextField } from "@mui/material"
import { Button } from "react-bootstrap"
import { Navigate, useLocation, useNavigate } from "react-router-dom"
import { AuthServices } from "../../services/AuthServices"
import { Error } from "../error/Error"
import { useAsync } from "../../http/Fetch"
import { ErrorAlertForm } from "../error/ErrorAlert"



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
    const [error, setError] = useState(false)

    const handleConfirmClick = useCallback((event:any) => {
        event.preventDefault()
        if (schoolId == null) {
            setError(true)
            return
        }
        navigate("/auth/register/student", {state: {schoolId: schoolId} })
    }, [navigate, schoolId])

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
                    <TextField label={"SchoolId"} required={true} type={"number"} onChange={(event) => setSchoolId(Number(event.target.value))}/>
                    <Button onClick={handleConfirmClick}> {"Confirm"} </Button>
                    <Button onClick={handleDeclineClick}> {"Decline"} </Button>
                    { error ? <ErrorAlertForm title="Please fill the form above." detail="Your school ID must not be empty." onClose={ () => setError(false) }/> : null }
                </>
            ) : null}
        </div>
    )
}

export function ShowCreateStudentFetchPost({
    authServices,
}: {
    authServices: AuthServices
}) {
    const location = useLocation()
    const content = useAsync(async () => {
        return await authServices.createStudentPost(location.state.schoolId)
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
    window.opener.postMessage({type:"Auth", data:'/auth/create/student'},process.env.NGROK_URI)
    window.close()
    return (<> </>)
}
