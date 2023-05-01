import * as React from "react";
import {useEffect, useState} from "react";
import {useAsync} from "./siren/Fetch";
import {ErrorMessageModel} from "./domain/response-models/Error";
import {SirenEntity} from "./siren/Siren";
import {Typography} from "@mui/material";
import {AuthServices} from "./services/AuthServices";
import {ErrorAlert} from "./ErrorAlert";
import {useNavigate} from "react-router-dom";
import {AuthState, useLoggedIn, useSetLogin} from "./Auth";

export function ShowAuthTeacherFetch({
    authServices,
}: {
    authServices: AuthServices;
}) {
    const content = useAsync(async () => {
        return await authServices.authTeacher();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const [windowRef, setWindowRef] = useState<Window>(null);
    const [isOpen, setOpen] = useState<Boolean>(false);
    const [data, setData] = useState<string>(null);
    const setLogin = useSetLogin()
    const navigate = useNavigate()

    useEffect(() => {
        if (content instanceof SirenEntity && !isOpen) {
            console.log("Opening window")
            console.log(content.properties.url)
            const authWindow = window.open(content.properties.url, '')
            setWindowRef(authWindow)
            setOpen(true)
        }
    }, [content, windowRef, setWindowRef, isOpen, setOpen])

    useEffect(() => {
        window.addEventListener('message', function(e) {
            if(e.origin !== 'http://localhost:3000')
                return;
            if (e.data.type === "Menu") {
               setLogin(AuthState.Teacher)
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
