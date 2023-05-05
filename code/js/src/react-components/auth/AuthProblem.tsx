import * as React from "react"
import { useCallback } from "react"
import { NavLink, useNavigate } from "react-router-dom"
import { useSetLogin } from "./Auth"
import {Box, Typography } from "@mui/material"
import { authServices } from ".."
import {homeBoxStyle, typographyStyle} from "../../utils/Style";

export function HandleAuthFailCallback() {
    window.opener.postMessage({type:"Auth", data:'/auth/fail'}, process.env.NGROK_URI)
    window.close()
    return (<> </>)
}

export function HandleAuthFail() {
    const navigate = useNavigate()
    const setLoggedin = useSetLogin()

    const handleLogout = useCallback(async() => {
        await authServices.logout()
        setLoggedin(undefined)
        navigate("/")
    }, [navigate, setLoggedin])

   return <>
        <Typography
            variant="h6"
            gutterBottom
        >
            You cannot login as a Student if you are a teacher or vice-versa. Please try again at <NavLink to={"/"}  onClick={handleLogout} className="navbar-brand" > Home </NavLink>
        </Typography>
   </>
}

export function HandleAuthErrorCallback() {
    window.opener.postMessage({type:"Auth", data:'/auth/error'}, process.env.NGROK_URI)
    window.close()
    return (<> </>)
}

export function HandleAuthError() {
    const navigate = useNavigate()
    const setLoggedin = useSetLogin()

    const handleLogout = useCallback(async() => {
        await authServices.logout()
        setLoggedin(undefined)
        navigate("/")
    }, [navigate, setLoggedin])

   return (
        <Box sx={homeBoxStyle}>
            <Typography
                variant="h6"
                gutterBottom
                sx={typographyStyle}
            >
                An error has happened. Please try again at <NavLink to={"/"}  onClick={handleLogout} className="navbar-brand" > Home </NavLink>
            </Typography>
        </Box>
   )
}
