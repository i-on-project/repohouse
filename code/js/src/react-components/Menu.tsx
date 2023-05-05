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
    List,
    ListItem,
    Modal,
    TextField,
    Typography,
    Button,
    IconButton,
    Accordion, AccordionSummary, AccordionDetails
} from "@mui/material"
import { MenuServices } from "../services/MenuServices"
import { Link, useParams } from "react-router-dom"
import { AuthState, toState, useLoggedIn } from "./auth/Auth"
import { Error } from "./error/Error"
import {homeBoxStyle, modalBoxStyle, modalButtonsBoxStyle, typographyStyle} from "../utils/Style";
import {CourseDtoProperties} from "../domain/dto/CourseDtoProperties";
import {mainTheme} from "../utils/Theme";
import GitHubIcon from "@mui/icons-material/GitHub";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

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
                        <Button sx={{mt:1,mb:2}} variant="contained" onClick={() => setIsOpened(true)}>
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
                            <Box sx={modalButtonsBoxStyle} >
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
                                <>
                                    <Grid item xs={5} md={2.75}>
                                        <CourseDetailsBox course={course} />
                                    </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid><Grid item xs={5} md={2.75}>
                                    <CourseDetailsBox course={course} />
                                </Grid>
                                </>
                            ))}
                        </Grid>
                    }
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

function CourseDetailsBox({course}: {course: CourseDtoProperties}) {

    const handleGithubClick = useCallback(() => {
        window.open(course.orgUrl, "_blank")
    }, [course.orgUrl])

    return (
        <Box
            sx={{
                border: 1,
                borderRadius: 1,
                borderColor: 'grey.500',
                p: 1,
                display:"flex",
                justifyContent:"center",
                alignItems:"center",
                flexDirection:"column",
                backgroundColor: mainTheme.palette.background.paper,
                '&:hover': {
                    backgroundColor: mainTheme.palette.primary.dark,
                    cursor: 'pointer',
                },
            }}
        >
            <Typography variant="h5">
                {course.name}
            </Typography>

            <IconButton>
                <GitHubIcon onClick={handleGithubClick}/>
            </IconButton>

            <Accordion
                square={false}
                sx={{
                    display:"flex",
                    justifyContent:"center",
                    alignItems:"center",
                    flexDirection:"column",
                    boxShadow: "none",
                    backgroundColor: mainTheme.palette.background.paper,
                    '&:hover': {
                        backgroundColor: mainTheme.palette.primary.dark,
                        cursor: 'pointer',
                    },
                }}
            >
                <AccordionSummary
                    expandIcon={<ExpandMoreIcon />}
                >
                    <Typography
                        variant="subtitle1"
                        sx={typographyStyle}
                    >
                        {"Teachers"}
                    </Typography>
                </AccordionSummary>
                <AccordionDetails>
                    <List>
                        {course.teacher.map( teacher => (
                            <ListItem>
                                <Typography
                                    variant="subtitle2"
                                    sx={typographyStyle}
                                >
                                    {teacher.name}
                                </Typography>
                            </ListItem>
                        ))}
                    </List>
                </AccordionDetails>
            </Accordion>
        </Box>
    )

}
