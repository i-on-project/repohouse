import * as React from "react"
import { useAsync } from "../http/Fetch"
import { useCallback, useState } from "react"
import { ErrorMessageModel } from "../domain/response-models/Error"
import { SirenEntity } from "../http/Siren"
import {Backdrop, Box, CircularProgress, List, ListItem, Modal, TextField, Typography } from "@mui/material"
import { MenuServices } from "../services/MenuServices"
import { Link, useParams } from "react-router-dom"
import { AuthState, toState, useLoggedIn } from "./auth/Auth"
import { Button } from "react-bootstrap"
import { Error } from "./error/Error"
import {homeBoxStyle} from "../utils/Style";

const style = {
    position: 'absolute' as 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
}


export function ShowMenuFetch({
    menuServices,
}: {
    menuServices: MenuServices
}) {

    const content = useAsync(async () => {
        return await menuServices.menu()
    })
    const [isOpened, setIsOpened] = useState(false)
    const [inviteCode, setInviteCode] = useState<string>('')
    const [error, setError] = useState(false)
    const loggedIn = useLoggedIn()

    const handleChangeInviteCode = useCallback(async (value) => {
        setInviteCode(value.target.value)
    }, [inviteCode,setInviteCode])

    const handleSubmit = useCallback(async () => {
        if (inviteCode === '') return
        const response = await menuServices.inviteLink(inviteCode)
        if (response instanceof ErrorMessageModel) {
            setError(true)
        } else {
            setIsOpened(false)
        }
    }, [inviteCode, setIsOpened, setError])

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

    if (content instanceof ErrorMessageModel || error) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
    }

    return (
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h2"
                    >
                        {"Menu"}
                    </Typography>
                   <Typography
                        variant="h6"
                        gutterBottom
                    >
                        {"Welcome " + content.properties.name}
                    </Typography>
                    { loggedIn === AuthState.Student ? (
                        <Button onClick={() => setIsOpened(true)}>
                            Invite Code
                        </Button>
                    ) : null}
                    <Modal
                        open={isOpened}
                        onClose={() => setIsOpened(false)}
                    >
                        <Box sx={style}>
                            <TextField
                                onChange={handleChangeInviteCode} value={inviteCode} id="inviteCode" label="InviteCode" variant="outlined"
                            />
                            <Button onClick={handleSubmit}>
                                Submit
                            </Button>
                            <Button onClick={() => setIsOpened(false)}>
                                Close
                            </Button>
                        </Box>
                    </Modal>
                    <Typography
                        variant="h6"
                        gutterBottom
                    >
                        {"Your Courses:"}
                    </Typography>
                    <List>
                        {content.properties.courses.map( course => (
                            <ListItem key={course.id}>
                                <Link to={"/courses/" + course.id}>{course.name}</Link>
                                <List>
                                    Teachers:
                                    {course.teacher.map( teacher => (
                                        <ListItem key={teacher.id}>
                                            {teacher.name}
                                        </ListItem>
                                    ))}
                                </List>
                            </ListItem>
                        ))}
                    </List>
                    { loggedIn === AuthState.Teacher ? (
                        <>
                            <Link to={"/teacher/orgs"}> Create Course </Link>
                            <br/>
                            <Link to={"/pending-teachers"}> Pending Teachers </Link>
                        </>
                    ) : null}
                </>
            ) : null}
        </Box>
    )
}

export function ShowMenuCallbackFetch() {
    const params = useParams()
    const state = toState(params.user)
    if (state !== undefined) {
        window.opener.postMessage({type:"Menu", data:'/menu', state: state}, process.env.NGROK_URI)
        window.close()
        return (<></>)
    }
   
    return <>
        It seems that your server redirect URL is not setup correctly!
    </>
}
