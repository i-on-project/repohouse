import * as React from "react"
import { useAsync } from "../http/Fetch"
import { useCallback, useState } from "react"
import { ErrorMessageModel } from "../domain/response-models/Error"
import { SirenEntity } from "../http/Siren"
import {
    Backdrop,
    Box,
    CircularProgress,
    Grid,
    Modal,
    TextField,
    Typography,
    Button,
    Avatar
} from "@mui/material"
import { MenuServices } from "../services/MenuServices"
import { Link, useNavigate, useParams, useSearchParams } from "react-router-dom"
import { AuthState, toState, useLoggedIn } from "./auth/Auth"
import { Error } from "./error/Error"
import {cardBoxStyle, homeBoxStyle, modalBoxStyle, alignHorizontalyBoxStyle, typographyStyle} from "../utils/Style";
import {CourseDtoProperties} from "../domain/dto/CourseDtoProperties";

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
                        variant="h6"
                        gutterBottom
                        sx={typographyStyle}
                    >
                        {"Welcome " + content.properties.name}
                    </Typography>
                    { loggedIn === AuthState.Student ? (
                        <Button sx={{mt:1,mb:2,}} variant="contained" onClick={() => setIsOpened(true)}>
                            Invite Code
                        </Button>
                    ) : null}
                    <Modal
                        open={isOpened}
                        onClose={() => setIsOpened(false)}
                    >
                        <Box sx={modalBoxStyle}>
                            <TextField
                                onChange={handleChangeInviteCode} value={inviteCode} id="inviteCode" label="InviteCode" variant="outlined"
                            />
                            <Box sx={alignHorizontalyBoxStyle} >
                                <Button variant="contained" onClick={handleSubmit}>
                                    Submit
                                </Button>
                                <Button
                                    variant="contained"
                                    onClick={() => setIsOpened(false)}
                                >
                                    Close
                                </Button>
                            </Box>
                        </Box>
                    </Modal>
                    {content.properties.courses.length === 0 ?
                        <Typography
                            variant="h6"
                            gutterBottom
                            sx={typographyStyle}
                        >
                            {"You are not enrolled in any courses"}
                        </Typography>
                    :
                        <Grid container
                              columnSpacing={{xs:1, md:3}}
                              rowSpacing={2}
                              direction="row"
                              alignItems="center"
                              justifyContent="center"
                        >
                            {content.properties.courses.map( course => (
                                <Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid>
                            ))}
                        </Grid>
                    }
                    { loggedIn === AuthState.Teacher ? (
                        <Box sx={alignHorizontalyBoxStyle}>
                            <Link to={"/teacher/orgs"}> Create Course </Link>
                            <Link to={"/pending-teachers"}> Pending Teachers </Link>
                        </Box>
                    ) : null}
                </>
            ) : null}
        </Box>
    )
}

export function ShowMenuCallbackFetch() {
    const params = useParams()
    const [searchParams] = useSearchParams()
    const state = toState(params.user)
    const githubId = searchParams.get('githubId')
    const userId = searchParams.get('userId')
    if (state !== undefined && githubId !== undefined && userId !== undefined) {
        window.opener.postMessage({type:"Menu", data:'/menu', state: {auth:state,githubId:githubId,userId:userId}}, process.env.NGROK_URI)
        window.close()
        return (<></>)
    }
   
    return (
        <Box sx={homeBoxStyle}>
            <Typography
                variant="h6"
                gutterBottom
                sx={typographyStyle}
            >
                It seems that your server redirect URL is not setup correctly!
            </Typography>
        </Box>
    )
}

function CourseDetailsBox({course}: {course: CourseDtoProperties}) {
    const navigate = useNavigate()

    const handleCourseClick = useCallback(() => {
        navigate("/courses/" + course.id)
    }, [course.id, navigate])

    return (
        <Box
            sx={cardBoxStyle}
            onClick={handleCourseClick}
        >
            <Box sx={alignHorizontalyBoxStyle} mb={2}>
                <Avatar/>
                <Typography variant="h5" sx={typographyStyle}>
                    {course.name}
                </Typography>
            </Box>
        </Box>
    )
}
