import * as React from "react"
import { useEffect, useState } from "react"
import { SirenEntity } from "../../http/Siren"
import { Typography } from "@mui/material"
import { AuthServices } from "../../services/AuthServices"
import { Navigate, useNavigate } from "react-router-dom"
import { AuthState, useLoggedIn, useSetLogin } from "./Auth"
import { useAsync } from "../../http/Fetch"

export function ShowAuthTeacherFetch({
    authServices,
}: {
    authServices: AuthServices
}) {

    const [windowRef, setWindowRef] = useState<Window>(null)
    const [isOpen, setOpen] = useState<Boolean>(false)
    const [data, setData] = useState<string>(null)
    const loggedin = useLoggedIn()
    const setLogin = useSetLogin()
    const navigate = useNavigate()
    const content = useAsync(async () => {
        if (!loggedin) return await authServices.authTeacher()
    })

    useEffect(() => {
        if (content instanceof SirenEntity && !isOpen) {
            console.log("Opening window")
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
                console.log(e)
               setLogin(AuthState.Teacher)
            }else {
                return
            }
            setData(e.data.data)
        }, false);
    }, [windowRef,useLoggedIn,useSetLogin])

    useEffect(() => {
        if (data) {
            navigate(data)
        }
    }, [data, navigate])

    if(loggedin) {
        return <Navigate to="/menu"/>
    }

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

    return (
        <div
            style={{
                alignItems: "center",
                justifyContent: "space-evenly",
            }}
        >
        </div>
    )
}
