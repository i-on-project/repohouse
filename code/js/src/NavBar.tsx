import * as React from 'react'
import { useCallback } from 'react'
import { useLoggedIn, useSetLogin } from './Auth'
import { NavLink, Outlet, useNavigate } from "react-router-dom"
import { AppBar, Box, Container, Toolbar } from "@mui/material"
import {AuthServices} from "./services/AuthServices";


export function NavBarShow({ authServices }: { authServices: AuthServices }) {
    const navigate = useNavigate()
    const loggedin = useLoggedIn()
    const setLoggedin = useSetLogin()

    const handleLogout = useCallback(async() => {
        await authServices.logout()
        setLoggedin(undefined)
        navigate("/")
    }, [navigate, setLoggedin])

    return (
        <>
            <AppBar position="static" color="transparent" >
                <Container maxWidth="xl">
                    <Toolbar disableGutters>
                        <NavLink to={"/"} className="navbar-brand"> ClassCode </NavLink>
                        <Box sx={{ display: { xs: 'flex', md: 'none' }, mr: 1 }} />
                        <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
                            {loggedin ? <NavLink to={"/menu"} className="navbar-brand"> Menu </NavLink> : null}
                            <NavLink to={"/credits"} className="navbar-brand"> Credits </NavLink>
                        </Box>
                        <Box sx={{ flexGrow: 0 }}>
                            {loggedin ?
                                <>
                                    <NavLink to={"/"}  onClick={handleLogout} className="navbar-brand" > Logout </NavLink>
                                </>: null
                            }
                        </Box>
                    </Toolbar>
                </Container>
            </AppBar>
            <Outlet/>
        </>
    )
}