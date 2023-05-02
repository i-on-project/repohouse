import * as React from "react"
import { useAsync } from "../http/Fetch"
import { useCallback, useState } from "react"
import { ErrorMessageModel } from "../domain/response-models/Error"
import { SirenEntity } from "../http/Siren"
import { Box, List, ListItem, Modal, TextField, Typography } from "@mui/material"
import { MenuServices } from "../services/MenuServices"
import { Link, useParams } from "react-router-dom"
import { ErrorAlert } from "./error/ErrorAlert"
import { AuthState, toState, useLoggedIn } from "./auth/Auth"
import { Button } from "react-bootstrap"

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
    const [error, setError] = useState<ErrorMessageModel>(null)
    const [isOpened, setIsOpened] = useState(false)
    const [inviteCode, setInviteCode] = useState<string>('')
    const loggedIn = useLoggedIn()

    const handleChangeInviteCode = useCallback(async (value) => {
        setInviteCode(value.target.value)
    }, [inviteCode,setInviteCode])

    const handleSubmit = useCallback(async () => {
        if (inviteCode === '') return
        const response = await menuServices.inviteLink(inviteCode)
        if (response instanceof ErrorMessageModel) {
            setError(response)
        } else {
            setIsOpened(false)
        }
    }, [inviteCode, setIsOpened, setError])

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
        setError(content)
    }

    return (
        <div
            style={{
                alignItems: "center",
                justifyContent: "space-evenly",
            }}
        >
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
                            <Link to={"/pending-teachers"}> Pending Teachers </Link>
                        </>
                    ) : null}
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => { setError(null) }}/>
        </div>
    )
}

export function ShowMenuCallbackFetch() {
    const params = useParams()
    const state = toState(params.user)
    if (state !== undefined) {
        window.opener.postMessage({type:"Menu", data:'/menu', state: state}, 'https://324b-2001-818-e975-8500-174-d17d-e3f5-574f.ngrok-free.app')
        window.close()
        return (<></>)
    }
   
    return <>
        It seems that your server redirect URL is not setup correctly!
    </>
}