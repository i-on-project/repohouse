import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useCallback, useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import {TextField, Typography} from "@mui/material";
import {Button} from "react-bootstrap";
import {Navigate, useNavigate} from "react-router-dom";
import {AuthServices} from "./services/AuthServices";
import {Label} from "@mui/icons-material";


export function ShowCreateFetch({
  authServices
}: {
    authServices: AuthServices;
}) {
    const content = useAsync(async () => {
        return await authServices.createTeacher();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const navigate = useNavigate();
    const [schoolId, setSchoolId] = useState<number>(null);
    const cookie:string = document.cookie  //TODO: undefined

    const handleConfirmClick = useCallback((event:any) => {
        event.preventDefault()
        console.log(cookie)
        if (cookie === "student") {
            ShowCreateStudentFetchPost({authServices, schoolId})
        } else if (cookie === "teacher") {
            ShowCreateTeacherFetchPost({authServices})
        }
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
        );
    }

    if (content instanceof ErrorMessageModel && !error) {
        setError(content);
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
                    {cookie === "student" ? (
                        <Label>
                            {"SchoolId"}
                            <TextField required type={"number"} onChange={(event) => setSchoolId(Number(event.target.value))}/>
                        </Label>
                    ) : null}
                    <Button onClick={handleConfirmClick}> {"Confirm"} </Button>
                    <Button onClick={handleDeclineClick}> {"Decline"} </Button>
                </>
            ) : null}
        </div>
    );
}


export function ShowCreateTeacherFetchPost({
    authServices,
}: {
    authServices: AuthServices;
}) {
    const content = useAsync(async () => {
        return await authServices.createTeacherPost();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);

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

    if (content instanceof ErrorMessageModel && !error) {
        setError(content);
    }

    return <Navigate to={"/auth/status"}/>
}


export function ShowCreateStudentFetchPost({
                                               authServices,
                                               schoolId
                                           }: {
    authServices: AuthServices;
    schoolId: number;
}) {
    const content = useAsync(async () => {
        return await authServices.createStudentPost(schoolId);
    });
    const [error, setError] = useState<ErrorMessageModel>(null);

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

    if (content instanceof ErrorMessageModel && !error) {
        setError(content);
    }

    return <Navigate to={"/auth/status"}/>
}

export function ShowCreateCallbackFetch(){
    window.opener.postMessage({type:"Auth", data:'http://localhost:3000/auth/create'},'http://localhost:3000/')
    window.close()
    return (<> </>)
}
