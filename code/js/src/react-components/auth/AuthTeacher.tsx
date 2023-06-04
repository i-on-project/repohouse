import * as React from "react"
import { useEffect, useState } from "react"
import { SirenEntity } from "../../http/Siren"
import { Backdrop, Box, CircularProgress } from "@mui/material"
import { AuthServices } from "../../services/AuthServices"
import { Navigate, useNavigate } from "react-router-dom"
import { AuthState, useLoggedIn, useSetGithubId, useSetLogin, useSetUserId } from "./Auth"
import { useAsync } from "../../http/Fetch"
import { homeBoxStyle } from "../../utils/Style"

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
    const setGithubId = useSetGithubId()
    const setUserId = useSetUserId()
    const navigate = useNavigate()
    const content = useAsync(async () => {
        if (!loggedin) return await authServices.authTeacher()
    })

    useEffect(() => {
        if (content instanceof SirenEntity && !isOpen) {
            const authWindow = window.open(content.properties.url, '')
            setWindowRef(authWindow)
            setOpen(true)
        }
    }, [content, windowRef, setWindowRef, isOpen, setOpen])

    useEffect(() => {
        window.addEventListener('message', function(e) {
            console.log(e)
            if(e.origin !== process.env.NGROK_URI)
                return;
            if (e.data.type === "Menu") {
                setGithubId(e.data.state.githubId)
                setUserId(e.data.state.userId)
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

    if(loggedin) {
        return <Navigate to="/menu"/>
    }

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

    return <Box sx={homeBoxStyle}/>
}
