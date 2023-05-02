import * as React from "react"
import { useAsync } from "../http/Fetch"
import { useCallback, useState } from "react"
import { ErrorMessageModel } from "../domain/response-models/Error"
import { SirenEntity } from "../http/Siren"
import { List, ListItem, Typography, Card, CardActionArea, CardContent } from "@mui/material"
import { CourseServices } from "../services/CourseServices"
import { Link, Navigate, useLocation, useNavigate } from "react-router-dom"
import { CourseBody } from "../domain/dto/CourseDtoProperties"
import { Button, Image } from "react-bootstrap"
import { GitHubOrg } from "../domain/response-models/GitHubOrgs"
import { AuthState, useLoggedIn } from "./auth/Auth"
import { Error } from "./error/Error"


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
            navigate("/menu")
        }
    }, [setError])

    const handleCreateClassroom = useCallback(async () => {
        navigate("/courses/" + courseId + "/classrooms/create");
    }, [navigate])

    if (content instanceof ErrorMessageModel || error) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
    }

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

    if (content instanceof ErrorMessageModel) {
        setError(true)
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
                        {content.properties.name}
                    </Typography>
                    <List>
                        {"Teachers"}
                        {content.properties.teacher.map( teacher => (
                            <ListItem key={teacher.id}>
                                {teacher.name}
                            </ListItem>
                        ))}
                    </List>
                    <a href={content.properties.orgUrl} target={"_blank"}>GitHub Page</a>
                    <List>
                        {content.properties.classrooms.map( classroom => (
                            <ListItem key={classroom.id}>

                                <Link to={"/courses/"+ courseId + "/classrooms/" + classroom.id}>
                                    {classroom.name}
                                </Link>
                            </ListItem>
                        ))}
                    </List>
                    { user == AuthState.Teacher  && !content.properties.isArchived ? (
                        <>
                            <Button onClick={handleCreateClassroom}>Create Classroom</Button>
                            <Button onClick={handleArchiveButton}>Archive</Button>
                        </>
                    ) : null}
                </>
            ) : null}
        </div>
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
    const [error, setError] = useState(true)
    
    const handleSubmit = useCallback((org: GitHubOrg) => {
        navigate("/courses/create", {state: {body: new CourseBody(org.login, org.url)} })
    }, [])

    if (content instanceof ErrorMessageModel || error) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
    }

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

    if (content instanceof ErrorMessageModel) {
        setError(true)
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
                        {"Create Course"}
                    </Typography>
                    <Typography
                        variant="h4"
                    >
                        {"Select an Organization:"}
                    </Typography>
                    {content.properties.orgs.map(org => <OrgsDetailsBox org={org} onClick={handleSubmit}/>)}
                    <Button onClick={() => navigate(-1)}>Back</Button>
                </>
            ) : null}
        </div>
    )
}

export function ShowCourseCreatePost({ courseServices }: { courseServices: CourseServices }) {
    const location = useLocation()
    const [error, setError] = useState(false)
    const content = useAsync(async () => {
        return await courseServices.createCourse(location.state.body) 
    })

    if (content instanceof ErrorMessageModel || error) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
    }

    if (!content) {
        return (
            <p>...loading...</p>
        )
    }

    if (content instanceof ErrorMessageModel) {
        setError(true)
    }

    if (content instanceof SirenEntity) {
        return <Navigate to={"/courses/" + content.properties.course.id} />
    }
}


function OrgsDetailsBox({ org, onClick } : { org: GitHubOrg, onClick: (org: GitHubOrg) => void }) {
    return (
        <>
            <Card variant="outlined">
                <CardActionArea onClick={(event: any) => {
                    event.preventDefault()
                    onClick(org)
                }}>
                    <CardContent>
                        <Typography variant="body2">
                            <Image src={org.avatar_url} style={{maxWidth:"15%", maxHeight:"15%"}}/>
                            <br/>
                            {org.login}
                        </Typography>
                    </CardContent>
                </CardActionArea>
                <a href={org.url} target="_blank" rel="noopener noreferrer">Open on Github</a>
            </Card>
        </>
    )
}
