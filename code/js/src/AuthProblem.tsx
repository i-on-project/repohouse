import * as React from "react";
import { useCallback } from "react";
import { authServices } from "./Dependecies";
import { NavLink, useNavigate } from "react-router-dom";
import { useSetLogin } from "./Auth";
import { Typography } from "@mui/material";

export function HandleAuthFailCallback() {
    window.opener.postMessage({type:"Auth", data:'/auth/fail'},'http://localhost:3000/')
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
    window.opener.postMessage({type:"Auth", data:'/auth/error'},'http://localhost:3000/')
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

   return <>
        <Typography
            variant="h6"
            gutterBottom
        >
            An Internal Server error happened. Please try again at <NavLink to={"/"}  onClick={handleLogout} className="navbar-brand" > Home </NavLink>
        </Typography>
   </>
}
