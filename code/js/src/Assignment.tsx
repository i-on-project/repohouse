import * as React from "react";
import {useCallback, useState} from "react";
import {useAsync} from "./siren/Fetch";
import {ErrorMessageModel} from "./domain/response-models/Error";
import {SirenEntity} from "./siren/Siren";
import {Box, ButtonGroup, CardActions, CardContent, TextField, Typography} from "@mui/material";
import {Link} from "react-router-dom";
import {Button, Card} from "react-bootstrap";
import {AssignmentServices} from "./services/AssignmentServices";
import {ErrorAlert} from "./ErrorAlert";
import {AuthState, useLoggedIn} from "./Auth";
import {
    AssignmentBody,
    StudentAssignmentDtoProperties,
    TeacherAssignmentDtoProperties
} from "./domain/dto/AssignmentDtoProperties";

export function ShowAssignmentFetch({
                                  assignmentServices,
                                    courseId,
                                    classroomId,
                                    assignmentId
                              }: {
    assignmentServices: AssignmentServices;
    courseId: number;
    classroomId: number;
    assignmentId: number

}) {
    const user = useLoggedIn()

    function renderAssignment() {
        switch (user) {
            case AuthState.Student:
                return <ShowStudentAssignmentFetch assignmentServices={assignmentServices} courseId={courseId} classroomId={classroomId} assignmentId={assignmentId}/>;
            case AuthState.Teacher:
                return <ShowTeacherAssignmentFetch assignmentServices={assignmentServices} courseId={courseId} classroomId={classroomId} assignmentId={assignmentId}/>
            default:
                return <ErrorAlert error={new ErrorMessageModel(401, "Unauthorized", "You are not logged in","")} onClose={() => {}}/>
        }
    }

    return (
        <div>
            { renderAssignment() }
        </div>
    )
}

function ShowStudentAssignmentFetch({
                                        assignmentServices,courseId,classroomId,assignmentId
                                    }: {
    assignmentServices: AssignmentServices;
    courseId: number;
    classroomId: number;
    assignmentId: number;
}) {

    const content = useAsync(async () => {
        return await assignmentServices.assignment(courseId,classroomId,assignmentId) as SirenEntity<StudentAssignmentDtoProperties >
    });
    const [error, setError] = useState<ErrorMessageModel>(null);

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
                        {content.properties.assignment.title}
                    </Typography>
                    <Typography
                        variant="h4"
                    >
                        {content.properties.assignment.description}
                    </Typography>
                    {content.properties.deliveries.map((delivery, index) => (
                        <Card>
                            <CardContent>
                                <Typography
                                    variant="h6"
                                >
                                    {"Delivery #" + index}
                                </Typography>
                                <Typography
                                    variant="h6"
                                >
                                    {delivery.tagControl + " -  " + new Date(delivery.dueDate).toLocaleString(
                                        "en-GB",
                                        {
                                            month: "long",
                                            day: "2-digit",
                                            year: "numeric",
                                        }
                                    )}
                                </Typography>
                            </CardContent>
                            <CardActions>
                                <Link to={"/assignments/" + content.properties.assignment.id + "/deliveries/" + delivery.id}>
                                    More Info
                                </Link>
                            </CardActions>
                        </Card>
                    ))}
                    {content.properties.team ? (
                        <Card>
                            <CardContent>
                                <Typography
                                    variant="h6"
                                    >
                                    {content.properties.team.name}
                                </Typography>
                            </CardContent>
                            <CardActions>
                                <Link to={"/teams/" + content.properties.team.id}>
                                    More Info
                                </Link>
                            </CardActions>
                        </Card>
                    ) : (
                        <Card>
                            <CardContent>
                                <Typography
                                    variant="h6"
                                >
                                    {"No team assigned - Please Join or Create one"}
                                </Typography>
                            </CardContent>
                            <CardActions>
                                <Link to={"/TODO"}>
                                    More Info
                                </Link>
                            </CardActions>
                        </Card>
                    )}
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </div>
    )
}

function ShowTeacherAssignmentFetch({
    assignmentServices,courseId,classroomId,assignmentId
}: {
    assignmentServices: AssignmentServices;
    courseId: number;
    classroomId: number;
    assignmentId: number;
}) {
    const content = useAsync(async () => {
        return await assignmentServices.assignment(courseId,classroomId,assignmentId) as SirenEntity<TeacherAssignmentDtoProperties>
    });
    const [error, setError] = useState<ErrorMessageModel>(null);

    const handleDeleteAssigment = useCallback(async () => {
        const response = await assignmentServices.deleteAssignment()
        if (response instanceof ErrorMessageModel) {
            setError(response)
        }
    }, [setError])


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
        setError(content);
    }



    return (
        <div
            style={{
                alignItems: "center",
                justifyContent: "space-evenly",
            }}
        >
            {content instanceof SirenEntity ?(
                <>
                    <Typography
                        variant="h2"
                    >
                        {content.properties.assignment.title}
                    </Typography>
                    <Typography
                        variant="h4"
                    >
                        {content.properties.assignment.description}
                    </Typography>
                    {content.properties.deliveries.map((delivery,index) => (
                        <Card>
                            <CardContent>
                                <Typography
                                    variant="h6"
                                >
                                    {"Delivery #" + index }
                                </Typography>
                                <Typography
                                    variant="h6"
                                >
                                    {delivery.tagControl + " -  " + delivery.dueDate}
                                </Typography>
                            </CardContent>
                            <CardActions>
                                <Link to={"/assignments/" + content.properties.assignment.id + "/deliveries/" + delivery.id}>
                                    More Info
                                </Link>
                            </CardActions>
                        </Card>
                    ))}
                    {content.properties.teams.map((team,index) => (
                        <Card>
                            <CardContent>
                                <Typography
                                    variant="h6"
                                >
                                    {team.name}
                                </Typography>
                            </CardContent>
                            <CardActions>
                                <Link to={"/teams/" + team.id}>
                                    More Info
                                </Link>
                            </CardActions>
                        </Card>
                    ))}
                    <Link to={"/assignments/" + content.properties.assignment.id + "/deliveries/create"}>Create Delivery</Link>
                    {content.properties.deliveries.length == 0 ? (
                        <Button onClick={handleDeleteAssigment}>Delete Assigment</Button>
                    ) : null}

                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </div>
    )
}

export function ShowCreateAssignment({ assignmentServices,classroomId, error }: { assignmentServices: AssignmentServices,classroomId:number, error: ErrorMessageModel | null }) {
    const [title, setTitle] = useState<string>(null)
    const [description, setDescription] = useState<string>(null)
    const [numbGroups, setNumbGroups] = useState(10)
    const [numbElemPerGroup, setNumbElemPerGroup] = useState(2)
    const [create, setCreate] = useState(false)
    const [serror, setError] = useState<ErrorMessageModel>(error)

    const increaseValue = useCallback((actualValue, fun) => {
        fun(actualValue + 1)
    }, [])

    const decreaseValue = useCallback((minValue,actualValue, fun) => {
        if (actualValue - 1 >= minValue) {
            fun(actualValue - 1)
        }
    }, [])

    const handleTitleChange = useCallback((value) => {
        setTitle(value.target.value)
    }, [setTitle])

    const handleDescriptionChange = useCallback((value) => {
        setDescription(value.target.value)
    }, [setDescription])

    const handleSubmit = useCallback((event:any) => {
        event.preventDefault()
        if (title == null || description == null) {
            return
        }
        setCreate(true)
    },[setCreate])

    if(create) {
        const assignment = new AssignmentBody(classroomId,numbElemPerGroup,numbGroups,title,description,new Date())
        return <ShowCreateAssignmentPost assignmentServices={assignmentServices} assignment={assignment}/>
    }

    const minNumbGroups = 5
    const minElemsPerGroup = 1

    return(
        <>
            <Typography variant="h3" component="h1" gutterBottom>
                Create Gamemode
            </Typography>

            <TextField onChange={handleTitleChange} value={title} id="title" label="Title" variant="outlined" required/>
            <TextField onChange={handleDescriptionChange} value={description} id="description" label="Description" variant="outlined" required/>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', p: 1, m: 1, bgcolor: 'background.paper' }}>
                <ButtonGroup variant="contained" aria-label="outlined primary button group">
                    <Button onClick={() => decreaseValue(minNumbGroups,numbGroups, setNumbGroups)}>-</Button>
                    <Button>{numbGroups}</Button>
                    <Button onClick={() => increaseValue( numbGroups, setNumbGroups)}>+</Button>
                </ButtonGroup>
                <Typography variant="h6" component="h1" gutterBottom>
                    Number of Groups
                </Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', p: 1, m: 1, bgcolor: 'background.paper' }}>
                <ButtonGroup variant="contained" aria-label="outlined primary button group">
                    <Button onClick={() => decreaseValue(minElemsPerGroup,numbElemPerGroup, setNumbElemPerGroup)}>-</Button>
                    <Button>{numbElemPerGroup}</Button>
                    <Button onClick={() => increaseValue(numbElemPerGroup, setNumbElemPerGroup)}>+</Button>
                </ButtonGroup>
                <Typography variant="h6" component="h1" gutterBottom>
                    Number of Elements per Group
                </Typography>
            </Box>
            <Button onClick={handleSubmit}>Create</Button>
            <ErrorAlert error={serror} onClose={() => { setError(null) }}/>
        </>
    )
}

function ShowCreateAssignmentPost({ assignmentServices, assignment }: { assignmentServices: AssignmentServices, assignment: AssignmentBody }) {
    const content = useAsync(async () => {
        return await assignmentServices.createAssignment(assignment);
    });
    const [error, setError] = useState<ErrorMessageModel>(null);


    if (!content) {
        return (
            <p>...loading...</p>
        )
    }

    if (content instanceof ErrorMessageModel) {
        setError(content)
    }

    if (content instanceof SirenEntity) {
        //TODO: redirect to the new assignment
    }
}
