import * as React from "react"
import { useAsync } from "./siren/Fetch"
import {useCallback, useState} from "react"
import { ErrorMessageModel } from "./domain/response-models/Error"
import { SirenEntity } from "./siren/Siren"
import {List, ListItem, Typography, Card, CardActionArea, CardContent} from "@mui/material"
import {CourseServices} from "./services/CourseServices"
import { Link, useLocation, useNavigate } from "react-router-dom"
import {CourseBody} from "./domain/dto/CourseDtoProperties"
import {Button, Image} from "react-bootstrap"
import { GitHubOrg } from "./domain/response-models/GitHubOrgs"
import {AuthState, useLoggedIn} from "./Auth";

export function ShowCourseFetch({
    courseServices, courseId
}: {
    courseServices: CourseServices;
    courseId: number;
}) {
    const content = useAsync(async () => {
        return await courseServices.course({courseId: courseId});
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const navigate = useNavigate();
    const user = useLoggedIn()

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

    const handleArchiveButton = async () => {
        const result = await courseServices.archiveCourse(courseId);
        if (result instanceof ErrorMessageModel) {
            setError(result);
        }
        if (result instanceof SirenEntity) {
            navigate("/menu")
        }
    }

    if (content instanceof ErrorMessageModel && !error) {
        setError(content);
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
                            <>
                                {console.log(classroom)}
                                <ListItem key={classroom.id}>

                                    <Link to={"/courses/"+ courseId + "/classrooms/" + classroom.id}>
                                        {classroom.name}
                                    </Link>
                                </ListItem>
                            </>
                        ))}
                    </List>
                    { user == AuthState.Teacher  && !content.properties.isArchived ? (
                        <Button onClick={handleArchiveButton}>Archive</Button>
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
    const [error, setError] = useState<ErrorMessageModel>(null)
    
    const handleSubmit = useCallback((org: GitHubOrg) => {
        navigate("/courses/create", {state: {body: new CourseBody(org.login, org.url)} })
    }, [])

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
                        {"Create Course"}
                    </Typography>
                    <Typography
                        variant="h4"
                    >
                        {"Select an Organization:"}
                    </Typography>
                    {content.properties.orgs.map(org => <OrgsDetailsBox org={org} onClick={handleSubmit}/>)}
                </>
            ) : null}
        </div>
    )
}

export function ShowCourseCreatePost({ courseServices }: { courseServices: CourseServices }) {
    const location = useLocation()
    const [error, setError] = useState<ErrorMessageModel>(null)
    const content = useAsync(async () => {
        return await courseServices.createCourse(location.state.body) 
    })

    if (!content) {
        return (
            <p>...loading...</p>
        )
    }

    if (content instanceof ErrorMessageModel && !error) {
        setError(content)
    }

    return <>Posted</>
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
                <a href={"https://github.com/" + org.login} target="_blank" rel="noopener noreferrer">Open on Github</a>
            </Card>
        </>
    )
}
