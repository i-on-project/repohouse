import * as React from "react"
import { useAsync } from "../http/Fetch"
import { useCallback, useState } from "react"
import { ErrorMessageModel } from "../domain/response-models/Error"
import { SirenEntity } from "../http/Siren"
import {
    List,
    ListItem,
    Typography,
    CircularProgress,
    Backdrop,
    Box, IconButton, Accordion, AccordionSummary, AccordionDetails, Grid, Button, ListItemAvatar, ListItemText, Avatar
} from "@mui/material"
import { CourseServices } from "../services/CourseServices"
import { Navigate, useLocation, useNavigate } from "react-router-dom"
import {CourseBody} from "../domain/dto/CourseDtoProperties"
import { Image } from "react-bootstrap"
import { GitHubOrg } from "../domain/response-models/GitHubOrgs"
import { AuthState, useLoggedIn } from "./auth/Auth"
import { Error } from "./error/Error"
import {
    accordionStyle,
    alignHorizontalyBoxStyle,
    cardBoxStyle,
    cardBoxStyle2,
    homeBoxStyle,
    typographyStyle
} from "../utils/Style";
import GitHubIcon from "@mui/icons-material/GitHub";
import { ClassroomDtoProperties } from "../domain/dto/ClassroomDtoProperties"
import { Teacher } from "../domain/User"
import { mainTheme } from "../utils/Theme";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";


export function ShowCourseFetch({
    courseServices, courseId
}: {
    courseServices: CourseServices
    courseId: number
}) {
    const content = useAsync(async () => {
        return await courseServices.course({courseId: courseId})
    })
    const [error, setError] = useState(false)
    const navigate = useNavigate()
    const user = useLoggedIn()

    const handleArchiveButton = useCallback(async () => {
        const result = await courseServices.archiveCourse(courseId)
        if (result instanceof ErrorMessageModel) {
            setError(true)
        }
        if (result instanceof SirenEntity) {
            navigate("/menu", { replace : true })
        }
    }, [setError])

    const handleLeaveCourse = useCallback(async () => {
        const result = await courseServices.leaveCourse(courseId)
        if (result instanceof ErrorMessageModel) {
            setError(true)
        }
        if (result instanceof SirenEntity) {
            navigate("/menu", { replace : true })
        }
    }, [setError])


    const handleGithubClick = useCallback((url) => {
        window.open(url, "_blank")
    }, [])

    const handleCreateClassroom = useCallback(async () => {
        navigate("/courses/" + courseId + "/classrooms/create", { replace : true });
    }, [navigate])

    if (content instanceof ErrorMessageModel || error) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
    }

    if (!content) {
        return (
            <Backdrop
                sx={{ color: 'primary', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                open={true}
            >
                <CircularProgress color="primary" />
            </Backdrop>
        );
    }

    if (content instanceof ErrorMessageModel) {
        setError(true)
    }

    return (
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ? (
                <>
                    <Box sx={alignHorizontalyBoxStyle}>
                        <Typography
                            variant="h2"
                            sx={typographyStyle}
                        >
                            {content.properties.name}
                        </Typography>
                        <IconButton>
                            <GitHubIcon onClick={() => handleGithubClick(content.properties.orgUrl)}/>
                        </IconButton>
                    </Box>
                    <TeachersDetailBox teachers={content.properties.teacher}/>
                    {content.properties.classrooms.length === 0 ?
                        <Typography
                            variant="h6"
                            gutterBottom
                            sx={typographyStyle}
                        >
                            {"You are not enrolled in any classroom"}
                        </Typography>
                        :
                        <Grid container
                              columnSpacing={{xs:1, md:3}}
                              rowSpacing={2}
                              direction="row"
                              alignItems="center"
                              justifyContent="center"
                        >
                            {content.properties.classrooms.map( classroom => (
                                <Grid item xs={5} md={2.75}>
                                    <ClassroomDetailsBox classroom={classroom} courseId={courseId}/>
                                </Grid>
                            ))}
                        </Grid>
                    }
                    { user == AuthState.Teacher  && !content.properties.isArchived ? (
                        <Box sx={alignHorizontalyBoxStyle}>
                            <Button variant="contained" onClick={handleCreateClassroom}>Create Classroom</Button>
                            <Button variant="contained" onClick={handleArchiveButton}>Archive</Button>
                        </Box>
                    ) : null}
                    { user == AuthState.Student  && !content.properties.isArchived ? (
                        <Box sx={alignHorizontalyBoxStyle}>
                            <Button variant="contained" onClick={handleLeaveCourse}>Leave Course</Button>
                        </Box>
                    ) : null}
                </>
            ) : null}
        </Box>
    );
}

export function ShowCourseCreateFetch({
    courseServices
}: {
    courseServices: CourseServices
}) {

    const content = useAsync(async () => {
        return await courseServices.getTeacherOrgs()
    })

    const navigate = useNavigate()
    const [error, setError] = useState(false)
    
    const handleSubmit = useCallback((org: GitHubOrg) => {
        navigate("/courses/create", {state: {body: new CourseBody(org.login, org.url, org.id)} })
    }, [])

    if (content instanceof ErrorMessageModel || error) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
    }

    if (!content) {
        return (
            <Backdrop
                sx={{ color: 'primary', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                open={true}
            >
                <CircularProgress color="primary" />
            </Backdrop>
        );
    }

    if (content instanceof ErrorMessageModel) {
        setError(true)
    }

    return (
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h5"
                        sx={typographyStyle}
                    >
                        {"Select an GitHub Organization"}
                    </Typography>
                    <Button variant="contained" onClick={() => navigate(-1)}>Back</Button>
                    <Grid
                        container
                        columnSpacing={{xs:1, md:3}}
                        rowSpacing={2}
                        direction="row"
                        alignItems="center"
                        justifyContent="center"
                    >
                        {content.properties.orgs.map(org =>
                            <Grid item xs={5} md={2.75}>
                                <OrgsDetailsBox org={org} onClick={handleSubmit}/>
                            </Grid>
                        )}
                    </Grid>
                </>
            ) : null}
        </Box>
    )
}

export function ShowCourseCreatePost({ courseServices }: { courseServices: CourseServices }) {
    const location = useLocation()
    const content = useAsync(async () => {
        return await courseServices.createCourse(location.state.body) 
    })

    if (content instanceof ErrorMessageModel) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
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

    if (content instanceof SirenEntity) {
        return <Navigate to={"/courses/" + content.properties.course.id} replace={true} />
    }
}


function OrgsDetailsBox({ org, onClick } : { org: GitHubOrg, onClick: (org: GitHubOrg) => void }) {

    return (
        <Box
            sx={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                flexDirection: "column",
                height: "100%",
                border: "1px solid black",
                borderRadius: 1,
                backgroundColor: mainTheme.palette.primary.main,
                '&:hover': {
                    backgroundColor: mainTheme.palette.primary.dark,
                    cursor: "pointer"
                }
            }}
            mt={2}
             onClick={() => onClick(org)}
        >
            <Typography variant="body2" sx={typographyStyle}>
                {org.login}
            </Typography>
            <Image src={org.avatar_url} style={{maxWidth:"15%", maxHeight:"15%",marginBottom:"6px"}}/>
        </Box>
    )
}


function ClassroomDetailsBox({classroom,courseId}: {classroom: ClassroomDtoProperties,courseId:number}) {
    const navigate = useNavigate()

    const handleCourseClick = useCallback(() => {
        navigate("/courses/"+ courseId + "/classrooms/" + classroom.id)
    }, [courseId,classroom.id,navigate])

    return (
        <Box
            sx={
                classroom.isArchived ?
                    cardBoxStyle2 :
                    cardBoxStyle
            }
            onClick={handleCourseClick}
        >
            <Typography variant="h5" sx={typographyStyle}>
                {classroom.name}
            </Typography>
            <Typography variant="subtitle2" sx={typographyStyle}>
                {"Last Updated: "+ new Date(classroom.lastSync).toLocaleString(
                    "en-GB",
                    {
                        hour: "2-digit",
                        minute: "2-digit",
                        month: "short",
                        day: "2-digit",
                        year: "numeric",
                    }
                )}
            </Typography>
        </Box>
    )
}

function TeachersDetailBox({teachers}: {teachers: Teacher[]}) {

    return (
        <Accordion
            square={false}
            sx={accordionStyle}
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
                <List
                    sx={{
                        maxHeight: 200,
                        position: 'relative',
                        overflow: 'auto'
                    }}
                >
                    {teachers.map( teacher => (
                        <ListItem key={teacher.id}>
                            <ListItemAvatar>
                                <Avatar src={"https://avatars.githubusercontent.com/u/" + teacher.githubId}/>
                            </ListItemAvatar>
                            <ListItemText
                                primary={teacher.name}
                                secondary={teacher.email}
                            />
                        </ListItem>
                    ))}
                </List>
            </AccordionDetails>
        </Accordion>
    )
}