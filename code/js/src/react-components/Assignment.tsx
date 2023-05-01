import * as React from "react";
import {useCallback, useState} from "react";
import {useAsync} from "../http/Fetch";
import {ErrorMessageModel} from "../domain/response-models/Error";
import {SirenEntity} from "../http/Siren";
import {Box, ButtonGroup, CardActions, CardContent, TextField, Typography} from "@mui/material";
import {Link, Navigate, useNavigate} from "react-router-dom";
import {Button, Card} from "react-bootstrap";
import {AssignmentServices} from "../services/AssignmentServices";
import {ErrorAlert} from "./error/ErrorAlert";
import {AuthState, useLoggedIn} from "./auth/Auth";
import {
    AssignmentBody,
    StudentAssignmentDtoProperties,
    TeacherAssignmentDtoProperties
} from "../domain/dto/AssignmentDtoProperties";
import {CreateTeamBody, JoinTeamBody} from "../domain/dto/RequestDtoProperties";
import {AssignmentDomain} from "../domain/Assignment";

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
                return <ErrorAlert error={new ErrorMessageModel("401", "Unauthorized", "You are not logged in")} onClose={() => {}}/>
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
                                <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +  "/deliveries/" + delivery.id}>
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
                                <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/teams/" + content.properties.team.id}>
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
                                <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/teams/"} state={{assignment:content.properties.assignment}}>
                                    Join or Create Team
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
    const navigate = useNavigate()

    const handleDeleteAssigment = useCallback(async () => {
        const response = await assignmentServices.deleteAssignment(courseId,classroomId,assignmentId)
        if (response instanceof ErrorMessageModel) {
            setError(response)
        }
        if (response instanceof SirenEntity) {
            console.log(response)
            if (response.properties.deleted) {
                navigate("/courses/" + courseId + "/classrooms/" + classroomId)
            }
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
                                <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/deliveries/" + delivery.id}>
                                    More Info
                                </Link>
                            </CardActions>
                        </Card>
                    ))}
                    {content.properties.teams.map((team) => (
                        <Card>
                            <CardContent>
                                <Typography
                                    variant="h6"
                                >
                                    {team.name}
                                </Typography>
                            </CardContent>
                            <CardActions>
                                <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +"/teams/" + team.id}>
                                    More Info
                                </Link>
                            </CardActions>
                        </Card>
                    ))}
                    <Link to={"/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/deliveries/create"}>Create Delivery</Link>
                    {content.properties.deliveries.length == 0 ? (
                        <Button onClick={handleDeleteAssigment}>Delete Assigment</Button>
                    ) : null}

                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </div>
    )
}

export function ShowCreateAssignment({ assignmentServices,courseId,classroomId, error }: { assignmentServices: AssignmentServices,courseId:number,classroomId:number, error: ErrorMessageModel | null }) {
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
        console.log("submit")
        if (title == null || description == null) {
            return
        }
        setCreate(true)
    },[setCreate, title, description])

    if(create) {
        console.log("create")
        const assignment = new AssignmentBody(classroomId,numbElemPerGroup,numbGroups,title,description,new Date())
        return <ShowCreateAssignmentPost assignmentServices={assignmentServices} assignment={assignment} courseId={courseId} classroomId={classroomId} error={serror}/>
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

function ShowCreateAssignmentPost({
                                      assignmentServices, assignment, courseId,classroomId,error
}: {
    assignmentServices: AssignmentServices,
    assignment: AssignmentBody,
    courseId:number,
    classroomId:number,
    error: ErrorMessageModel
}) {
    const content = useAsync(async () => {
        return await assignmentServices.createAssignment(courseId,classroomId,assignment);
    });
    const [serror, setError] = useState<ErrorMessageModel>(error);


    if (!content) {
        return (
            <p>...loading...</p>
        )
    }

    if (serror){
        return <ErrorAlert error={serror} onClose={() => setError(null)}/>
    }

    if (content instanceof ErrorMessageModel) {
        setError(content)
    }

    if (content instanceof SirenEntity) {
        const assigmentId = content.properties.assignment.id
        return <Navigate to={`/courses/${courseId}/classrooms/${classroomId}/assignments/${assigmentId}`}/>
    }
}

export function ShowAssigmentTeamsFetch({
        assignmentServices,
        courseId,
        classroomId,
        assignment,
        error
}:{
    assignmentServices:AssignmentServices,
    courseId:number,
    classroomId:number,
    assignment:AssignmentDomain,
    error:ErrorMessageModel
}){

    const content = useAsync(async () => {
        return await assignmentServices.teams(courseId,classroomId,assignment.id);
    });

    const [serror, setError] = useState<ErrorMessageModel>(error);
    const [teamName, setTeamName] = useState<string>(null)
    const navigate = useNavigate()

    const handleJoinTeam = useCallback((event:any) => {
        event.preventDefault()
        const teamId = event.target.value
        const body = new JoinTeamBody(assignment.id,teamId)
        const response = assignmentServices.joinTeam(body,courseId,classroomId,assignment.id)
        if(response instanceof ErrorMessageModel) {
            setError(response)
        }
        if (response instanceof SirenEntity) {
            navigate("/courses/" + courseId +"/classrooms/"+classroomId+"/assignments/"+assignment.id+"teams/"+teamId, { replace: true })
        }
    },[setError])

    const handleCreateTeam = useCallback((event:any) => {
        event.preventDefault()
        const body = new CreateTeamBody(null)
        const response = assignmentServices.createTeam(body,courseId,classroomId,assignment.id)
        if(response instanceof ErrorMessageModel) {
            setError(response)
        }
        if (response instanceof SirenEntity) {
            //TODO: Check this, maybe id is not the correct property
            navigate("/courses/" + courseId +"/classrooms/"+classroomId+"/assignments/"+assignment.id+"teams/"+response.properties.id, { replace: true })
        }
    },[setError])

    if (!content) {
        return (
            <p>...loading...</p>
        )
    }

    return(
        <>
            {content instanceof SirenEntity ?(
                <>
                <Typography variant="h3" component="h1" gutterBottom>
                    {assignment.title}
                </Typography>
                <Typography variant="h4" component="h1" gutterBottom>
                    Join a Team
                </Typography>
                {content.properties.teams.map((team) => (
                    <Card>
                        <CardContent>
                            <Typography
                                variant="h6"
                            >
                                {team.team.name}
                            </Typography>
                            <Typography
                                variant="h6"
                            >
                                {team.students.map((student) => (
                                    <Typography
                                        variant="h6"
                                    >
                                        {student.name}
                                    </Typography>
                                ))}
                            </Typography>
                        </CardContent>
                        <CardActions>
                            <Button onClick={handleJoinTeam} value={team.team.id} disabled={team.students.length >= assignment.maxElemsPerGroup}>Join</Button>
                        </CardActions>
                    </Card>
                ))}
                    Or
                <Typography variant="h4" component="h1" gutterBottom>
                    Create a Team
                </Typography>
                {content.properties.teams.length < assignment.maxNumberGroups ? (
                    <>
                        <Button onClick={handleCreateTeam}>Create</Button>
                    </>
                ): (
                    <Typography variant="h6" component="h1" gutterBottom>
                        You can't create a team, max number of teams reached
                    </Typography>
                )}
                </>
            ):null}
            <ErrorAlert error={serror} onClose={() => setError(null)}/>
        </>
    )
}
