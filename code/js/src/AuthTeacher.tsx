import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useEffect, useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import {Typography} from "@mui/material";
import {AuthServices} from "./services/AuthServices";
import {ErrorAlert} from "./ErrorAlert";
import {useNavigate} from "react-router-dom";
import {useSetLogin} from "./Auth";

export function ShowAuthTeacherFetch({
                                         authServices,
                                     }: {
    authServices: AuthServices;
}) {
    console.log("ShowAuthTeacherFetch")
    const content = useAsync(async () => {
        console.log("ShowAuthTeacherFetch - useAsync")
        return await authServices.authTeacher();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const [windowRef, setWindowRef] = useState<Window>(null);
    const [isOpen, setOpen] = useState<Boolean>(false);
    const navigate = useNavigate()
    const setLogin = useSetLogin()

    useEffect(() => {
        if (content instanceof SirenEntity && !isOpen) {
            console.log("Url - " + content)
            const authWindow = window.open(content.properties.url, '')
            setWindowRef(authWindow)
            setOpen(true)
        }
    }, [content, windowRef, setWindowRef, isOpen, setOpen])

    useEffect(() => {
        window.addEventListener('message', function(e) {
            if(e.origin !== 'http://localhost:3000')
                return;
            if (e.data.type === "Auth") {
                navigate(e.data.data)
            }
        }, false);
    }, [windowRef])

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