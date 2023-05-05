import * as React from 'react'
import { useCallback, useState } from 'react'
import { AuthState, useLoggedIn, useSetLogin } from './auth/Auth'
import { NavLink, Outlet, useNavigate } from "react-router-dom"
import { AppBar, Avatar, Box, Button, Container, IconButton, Menu, MenuItem, ThemeProvider, Toolbar, Tooltip, Typography } from "@mui/material"
import { AuthServices } from "../services/AuthServices"
import MenuIcon from '@mui/icons-material/Menu'
import LoginIcon from '@mui/icons-material/Login'
import { mainTheme } from "../utils/Theme"


export function NavBarShow({ authServices }: { authServices: AuthServices }) {
    const navigate = useNavigate()
    const loggedin = useLoggedIn()
    const setLoggedin = useSetLogin()

    const handleLogout = useCallback(async() => {
        await authServices.logout()
        setLoggedin(AuthState.None)
        navigate("/")
    }, [navigate, setLoggedin])

    const [anchorElNav, setAnchorElNav] = useState(false)
    const [anchorElUser, setAnchorElUser] = useState(false)

    const handleOpenNavMenu = useCallback(() => {
        setAnchorElNav(true)
    }, [setAnchorElNav])

    const handleCloseNavMenu = useCallback(() => {
        setAnchorElNav(false)
    }, [setAnchorElNav])

    const handleOpenUserMenu = useCallback(() => {
        setAnchorElUser(true)
    }, [setAnchorElUser])

    const handleCloseUserMenu  = useCallback(() => {
        setAnchorElUser(false)
    }, [setAnchorElUser])

    const handleHomeClick = useCallback( () => {
        navigate("/")
    },[navigate])

    return (
        <>
            <ThemeProvider theme={mainTheme}>
            <AppBar position="fixed" elevation={4}>
                <Container maxWidth="xl">
                    <Toolbar disableGutters>
                        <Avatar alt="logo" src="/project-logo-icon.ico" sx={{ display: { xs: 'none', md: 'flex' }, mr: 1 }} />
                        <Typography
                            variant="h6"
                            noWrap
                            component="span"
                            onClick={handleHomeClick}
                            sx={{
                                mr: 2,
                                display: { xs: 'none', md: 'flex' },
                                fontFamily: 'monospace',
                                fontWeight: 700,
                                letterSpacing: '.3rem',
                                color: 'inherit',
                                textDecoration: 'none',
                            }}
                        >
                            ClassCode
                        </Typography>
                        <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
                            <IconButton
                                size="large"
                                aria-label="account of current user"
                                aria-controls="menu-appbar"
                                aria-haspopup="true"
                                onClick={handleOpenNavMenu}
                                color="inherit"
                            >
                                <MenuIcon />
                            </IconButton>
                            <Menu
                                id="menu-appbar"
                                anchorOrigin={{
                                    vertical: 'top',
                                    horizontal: 'left',
                                }}
                                keepMounted
                                transformOrigin={{
                                    vertical: 'top',
                                    horizontal: 'left',
                                }}
                                open={Boolean(anchorElNav)}
                                onClose={handleCloseNavMenu}
                                sx={{
                                    display: { xs: 'block', md: 'none' },
                                }}
                            >
                                <div key="menu-keys">
                                    {loggedin ? (
                                        <MenuItem key={"menu"} onClick={handleCloseNavMenu}>
                                            <Typography textAlign="center">
                                                <NavLink to={"/menu"} style={{textDecoration:"none",color:mainTheme.palette.primary.contrastText}}> Menu </NavLink>
                                            </Typography>
                                        </MenuItem>
                                    ) : null}
                                    <MenuItem key={"credits"} onClick={handleCloseNavMenu}>
                                        <Typography textAlign="center">
                                            <NavLink to={"/credits"} style={{textDecoration:"none",color:mainTheme.palette.primary.contrastText}}> Credits </NavLink>
                                        </Typography>
                                    </MenuItem>
                                </div>
                            </Menu>
                        </Box>
                        <Avatar alt="logo" src="/project-logo-icon.ico" sx={{ display: { xs: 'flex', md: 'none' }, mr: 1 }} />
                        <Typography
                            variant="h5"
                            noWrap
                            component="span"
                            onClick={handleHomeClick}
                            sx={{
                                mr: 2,
                                display: { xs: 'flex', md: 'none' },
                                flexGrow: 1,
                                fontFamily: 'monospace',
                                fontWeight: 700,
                                letterSpacing: '.3rem',
                                color: 'inherit',
                                textDecoration: 'none',
                            }}
                        >
                            ClassCode
                        </Typography>
                        <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
                            {loggedin ?
                                <Button
                                    key={"menu"}
                                    onClick={handleCloseNavMenu}
                                    sx={{ my: 2, color: 'white', display: 'block' }}
                                >
                                    <NavLink to={"/menu"} style={{textDecoration:"none",color:mainTheme.palette.primary.contrastText}}> Menu </NavLink>
                                </Button>
                            :null}
                            <Button
                                key={"credits"}
                                onClick={handleCloseNavMenu}
                                sx={{ my: 2, color: 'white', display: 'block' }}
                            >
                                <NavLink to={"/credits"} style={{textDecoration:"none",color:mainTheme.palette.primary.contrastText}}> Credits </NavLink>
                            </Button>
                        </Box>

                        <Box sx={{ flexGrow: 0 }}>
                            <Tooltip title="Open settings">
                                <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                                    {loggedin ? <Avatar alt="UserLogo" src="/static/images/avatar/2.jpg" />
                                        : <LoginIcon/>
                                    }
                                </IconButton>
                            </Tooltip>
                            <Menu
                                sx={{ mt: '45px' }}
                                id="menu-appbar"
                                anchorOrigin={{
                                    vertical: 'top',
                                    horizontal: 'right',
                                }}
                                keepMounted
                                transformOrigin={{
                                    vertical: 'top',
                                    horizontal: 'right',
                                }}
                                open={Boolean(anchorElUser)}
                                onClose={handleCloseUserMenu}
                            >
                                <div key="menu-keys">
                                    {loggedin ?
                                        <MenuItem onClick={handleLogout}>
                                            Logout
                                        </MenuItem>
                                        :
                                        <>
                                            <MenuItem>
                                                <NavLink to={"/auth/student"} style={{textDecoration:"none",color:mainTheme.palette.primary.contrastText}}> Student </NavLink>
                                            </MenuItem>
                                            <MenuItem>
                                                <NavLink to={"/auth/teacher"} style={{textDecoration:"none",color:mainTheme.palette.primary.contrastText}}> Teacher </NavLink>
                                            </MenuItem>
                                        </>
                                    }
                                </div>
                            </Menu>
                        </Box>
                    </Toolbar>
                </Container>
            </AppBar>
            <Outlet/>
            </ThemeProvider>
        </>
    )
}
