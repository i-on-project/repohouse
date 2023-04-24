import * as React from "react";
import {useCallback, useEffect, useState} from "react";
import {useAsync} from "./siren/Fetch";
import {ErrorMessageModel} from "./domain/response-models/Error";
import {SirenEntity} from "./siren/Siren";
import {Typography} from "@mui/material";
import {AuthServices} from "./services/AuthServices";
import {useNavigate} from "react-router-dom";
import {ErrorAlert} from "./ErrorAlert";
import {AuthState, useLoggedIn, useSetLogin} from "./Auth";

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
            if(e.origin !== 'http://localhost:3000')
                return;
            if (e.data.type === "Menu") {
                setLogin(AuthState.Student)
            }
            console.log(e.data.data)
            setData(e.data.data)
        }, false);
    }, [windowRef,useLoggedIn,useSetLogin])

    useEffect(() => {
        if (data) {
            console.log("Navigating to " + data)
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