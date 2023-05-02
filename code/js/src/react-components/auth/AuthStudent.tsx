import * as React from "react"
import { useEffect, useState } from "react"
import { useAsync } from "../../http/Fetch"
import { ErrorMessageModel } from "../../domain/response-models/Error"
import { SirenEntity } from "../../http/Siren"
import { Typography } from "@mui/material"
import { AuthServices } from "../../services/AuthServices"
import { useNavigate } from "react-router-dom"
import { ErrorAlert } from "../error/ErrorAlert"
import { AuthState, useLoggedIn, useSetLogin } from "./Auth"

export function ShowAuthStudentFetch({
    authServices,
}: {
    authServices: AuthServices;
}) {
    const content = useAsync(async () => {
        return await authServices.authStudent();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const [windowRef, setWindowRef] = useState<Window>(null);
    const [isOpen, setOpen] = useState<Boolean>(false);
    const [data, setData] = useState<string>(null);
    const navigate = useNavigate()
    const setLogin = useSetLogin()

    useEffect(() => {
        if (content instanceof SirenEntity && !isOpen) {
            const authWindow = window.open(content.properties.url, '')
            setWindowRef(authWindow)
            setOpen(true)
        }
    }, [content, windowRef, setWindowRef, isOpen, setOpen])

    useEffect(() => {
        window.addEventListener('message', function(e) {
            if(e.origin !== process.env.FRONTEND_NGROK_KEY)
                return;
            if (e.data.type === "Menu") {
                setLogin(AuthState.Student)
            }
            setData(e.data.data)
        }, false);
    }, [windowRef,useLoggedIn,useSetLogin])

    useEffect(() => {
        if (data) {
            navigate(data)
        }
    }, [data, navigate])

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
            <ErrorAlert error={error} onClose={() => { setError(null) }}/>
        </div>
    );
}