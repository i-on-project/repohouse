import * as React from "react";
import {useCallback, useState} from "react";
import {useAsync} from "../http/Fetch";
import {ErrorMessageModel} from "../domain/response-models/Error";
import {SirenEntity} from "../http/Siren";
import {
    Backdrop,
    Box,
    ButtonGroup,
    CardActions,
    CardContent,
    CircularProgress, Grid,
    TextField,
    Button,
    Typography, Accordion, AccordionSummary, AccordionDetails, List, ListItem,
} from "@mui/material";
import {Link, Navigate, useNavigate} from "react-router-dom";
import {Card} from "react-bootstrap";
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
import {accordionStyle, alignHorizontalyBoxStyle, homeBoxStyle, typographyStyle} from "../utils/Style";
import {mainTheme} from "../utils/Theme";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

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
    const navigate = useNavigate();
    const [error, setError] = useState<ErrorMessageModel>(null);

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

    if (content instanceof ErrorMessageModel && !error) {
        setError(content);
    }

    return (
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h2"
                        sx={typographyStyle}
                    >
                        {content.properties.assignment.title}
                    </Typography>
                    <Typography
                        variant="h6"
                        sx={typographyStyle}
                    >
                        {content.properties.assignment.description}
                    </Typography>
                    {content.properties.team ? (
                            <Box
                                sx={{display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center","&:hover":{cursor:"pointer"}}}
                                onClick={() => navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/teams/" + content.properties.team.id)}
                            >
                                <Typography
                                    variant="subtitle1"
                                    sx={typographyStyle}
                                >
                                    {content.properties.team.name}
                                </Typography>
                            </Box>
                    ) : (

                        <Box
                            sx={alignHorizontalyBoxStyle}
                            mb={1}
                            m={1}
                        >
                            <Typography
                                variant="subtitle2"
                                sx={typographyStyle}
                            >
                                {"No team assigned - Please Join or Create one"}
                            </Typography>
                            <Button
                                variant="contained"
                                size="small"
                                onClick={() => navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/teams/",{state:{assignment:content.properties.assignment}})}
                            >
                                Join or Create Team
                            </Button>
                        </Box>
                    )}
                    <Grid
                        container
                        spacing={0}
                        direction="row"
                        alignItems="flex-start"
                        justifyContent="center"
                    >
                            {content.properties.deliveries.map((delivery, index) => (
                                <Grid item xs={12} md={5}>
                                    <Card
                                        key={index}
                                        onClick={() => navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId +  "/deliveries/" + delivery.id)}
                                    >
                                        <CardContent
                                            sx={{
                                                display: "flex",
                                                flexDirection: "column",
                                                alignItems: "center",
                                                justifyContent: "center",
                                                border: "1px solid black",
                                                borderRadius: "5px",
                                                maxWidth:{xs:"30%",md:"75%"},
                                                margin:"auto",
                                                mb:2,
                                                backgroundColor: mainTheme.palette.secondary.light,
                                                '&:hover': {
                                                    backgroundColor: mainTheme.palette.secondary.main,
                                                    cursor: "pointer"
                                                }
                                            }}

                                        >
                                            <Typography
                                                variant="subtitle1"
                                                sx={typographyStyle}
                                            >
                                                {"Delivery #" + (index + 1)}
                                            </Typography>
                                            <Typography
                                                variant="subtitle1"
                                                sx={typographyStyle}
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
                                    </Card>
                            </Grid>
                        ))}
                    </Grid>
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </Box>
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
            if (response.properties.deleted) {
                navigate("/courses/" + courseId + "/classrooms/" + classroomId)
            }
        }
    }, [setError])

    const handleCreateDelivery = useCallback(async () => {
       navigate("/courses/"+ courseId + "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/deliveries/create")
    }, [])

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

    if (content instanceof ErrorMessageModel && !error) {
        setError(content);
    }

    return (
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ?(
                <>
                    <Typography
                        variant="h2"
                        sx={typographyStyle}
                    >
                        {content.properties.assignment.title}
                    </Typography>
                    <Typography
                        variant="h5"
                        sx={typographyStyle}
                    >
                        {content.properties.assignment.description}
                    </Typography>
                    <Box sx={alignHorizontalyBoxStyle}>
                        <Button variant="contained" onClick={handleCreateDelivery}>Create Delivery</Button>
                        {content.properties.deliveries.length == 0 ? (
                            <Button variant="contained" onClick={handleDeleteAssigment}>Delete Assignment</Button>
                        ) : null}
                    </Box>
                    {content.properties.deliveries.map((delivery,index) => (
                        <Box
                            onClick={() => navigate("/courses/"+ courseId+ "/classrooms/" + classroomId +"/assignments/" + assignmentId + "/deliveries/" + delivery.id)}
                            sx={{
                                display:"flex",
                                flexDirection:"column",
                                alignItems:"center",
                                justifyContent:"center",
                                border:"1px solid black",
                                borderRadius:"5px",
                                backgroundColor: mainTheme.palette.secondary.light,
                                '&:hover': {
                                    backgroundColor: mainTheme.palette.secondary.main,
                                    cursor: "pointer"
                                    }
                            }}
                        >
                                <Typography
                                    variant="h6"
                                    sx={typographyStyle}
                                >
                                    {"Delivery #" + index }
                                </Typography>
                                <Typography
                                    variant="h6"
                                    sx={typographyStyle}
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
                        </Box>
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
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => setError(null)}/>
        </Box>
    )
}

export function ShowCreateAssignment({ assignmentServices,courseId,classroomId, error }: { assignmentServices: AssignmentServices,courseId:number,classroomId:number, error: ErrorMessageModel | null }) {
    const [title, setTitle] = useState<string>(null)
    const [description, setDescription] = useState<string>(null)
    const [numbGroups, setNumbGroups] = useState(10)
    const [minNumbElemPerGroup, setMinNumbElemPerGroup] = useState(1)
    const [maxNumbElemPerGroup, setMaxNumbElemPerGroup] = useState(1)
    const [create, setCreate] = useState(false)
    const [serror, setError] = useState<ErrorMessageModel>(error)

    const minNumbGroups = 5
    const minElemsPerGroup = 1

    const handleMinNumbElemsDecrease = useCallback(() => {
        decreaseValue(1,minNumbElemPerGroup,setMinNumbElemPerGroup)
    }, [minNumbElemPerGroup,setMinNumbElemPerGroup])

    const handleMaxNumbElemsIncrese = useCallback(() => {
        increaseValue(maxNumbElemPerGroup,setMaxNumbElemPerGroup)
    }, [maxNumbElemPerGroup,setMaxNumbElemPerGroup])

    const handleMinNumbElemsIncrese = useCallback(() => {
        if (minNumbElemPerGroup + 1 == maxNumbElemPerGroup) {
            handleMaxNumbElemsDecrease()
        }
        increaseValue(minNumbElemPerGroup,setMinNumbElemPerGroup)
    }, [minNumbElemPerGroup,maxNumbElemPerGroup,setMinNumbElemPerGroup,setMaxNumbElemPerGroup])

    const handleMaxNumbElemsDecrease = useCallback(() => {
        if (maxNumbElemPerGroup - 1 == minNumbElemPerGroup) {
            handleMinNumbElemsDecrease()
        }
        decreaseValue(minNumbElemPerGroup,maxNumbElemPerGroup,setMaxNumbElemPerGroup)
    }, [minNumbElemPerGroup,maxNumbElemPerGroup,setMinNumbElemPerGroup,setMaxNumbElemPerGroup])


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
    },[setCreate,title, description])

    if(create) {
        const assignment = new AssignmentBody(classroomId,minNumbElemPerGroup,maxNumbElemPerGroup,numbGroups,title,description,new Date())
        return <ShowCreateAssignmentPost assignmentServices={assignmentServices} assignment={assignment} courseId={courseId} classroomId={classroomId} error={serror}/>
    }

    return(
        <Box sx={homeBoxStyle}>
            <Typography variant="h3" component="h1" gutterBottom sx={typographyStyle}>
                Create Assignment
            </Typography>

            <TextField onChange={handleTitleChange} value={title} id="title" label="Title" variant="outlined" required sx={{margin:"6px"}}/>
            <TextField onChange={handleDescriptionChange} value={description} id="description" label="Description" variant="outlined" required/>
            <Box sx={alignHorizontalyBoxStyle}>
                <ButtonGroup variant="contained" aria-label="outlined primary button group">
                    <Button onClick={() => decreaseValue(minNumbGroups,numbGroups, setNumbGroups)}>-</Button>
                    <Button>{numbGroups}</Button>
                    <Button onClick={() => increaseValue( numbGroups, setNumbGroups)}>+</Button>
                </ButtonGroup>
                <Typography variant="h6" component="h1" gutterBottom  sx={typographyStyle}>
                    Number of Groups
                </Typography>
            </Box>
            <Box sx={alignHorizontalyBoxStyle}>
                <ButtonGroup variant="contained" aria-label="outlined primary button group">
                    <Button onClick={() => decreaseValue(minElemsPerGroup,minNumbElemPerGroup, setMinNumbElemPerGroup)}>-</Button>
                    <Button>{minNumbElemPerGroup}</Button>
                    <Button onClick={() => increaseValue(minNumbElemPerGroup, setMinNumbElemPerGroup)}>+</Button>
                </ButtonGroup>
                <Typography variant="h6" component="h1" gutterBottom sx={typographyStyle}>
                    Minimum Number of Elements per Group
                </Typography>
            </Box>
            <Box sx={alignHorizontalyBoxStyle}>
                <ButtonGroup variant="contained" aria-label="outlined primary button group">
                    <Button onClick={() => decreaseValue(maxNumbElemPerGroup,maxNumbElemPerGroup, setMaxNumbElemPerGroup)}>-</Button>
                    <Button>{maxNumbElemPerGroup}</Button>
                    <Button onClick={() => increaseValue(maxNumbElemPerGroup, setMaxNumbElemPerGroup)}>+</Button>
                </ButtonGroup>
                <Typography variant="h6" component="h1" gutterBottom sx={typographyStyle}>
                    Maximum Number of Elements per Group
                </Typography>
            </Box>
            <Button variant="contained" onClick={handleSubmit}>Create</Button>
            <ErrorAlert error={serror} onClose={() => { setError(null) }}/>
        </Box>
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
            <Backdrop
                sx={{ color: 'primary', zIndex: (theme) => theme.zIndex.drawer + 1 }}
                open={true}
            >
                <CircularProgress color="primary" />
            </Backdrop>
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
        return await assignmentServices.teams(courseId,classroomId,assignment.id)
    });

    const [serror, setError] = useState<ErrorMessageModel>(error)
    const navigate = useNavigate()

    const handleJoinTeam = useCallback(async (event:any) => {
        event.preventDefault()
        const teamId = event.target.value
        const body = new JoinTeamBody(assignment.id,teamId)
        const response = await assignmentServices.joinTeam(body,courseId,classroomId,assignment.id)
        if(response instanceof ErrorMessageModel) {
            setError(response)
        }
        if (response instanceof SirenEntity) {
            navigate("/courses/" + courseId +"/classrooms/"+classroomId+"/assignments/"+assignment.id+"/teams/"+teamId, { replace: true })
        }
    },[setError])

    const handleCreateTeam = useCallback(async (event: any) => {
        event.preventDefault()
        const body = new CreateTeamBody(null)
        const response = await assignmentServices.createTeam(body, courseId, classroomId, assignment.id)

        if (response instanceof ErrorMessageModel) {
            setError(response)
        }
        if (response instanceof SirenEntity) {
            navigate("/courses/" + courseId + "/classrooms/" + classroomId + "/assignments/" + assignment.id + "/teams/" + response.properties.teamId, {replace: true})
        }
    },[setError])

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

    return(
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ?(
                <>
                <Typography variant="h3" component="h1" gutterBottom>
                    {assignment.title}
                </Typography>
                <Grid
                    container
                    direction="row"
                    justifyContent="center"
                    alignItems="center"
                >
                    <Grid item md={5} xs={10}>
                    <Typography variant="h4" component="h1" gutterBottom sx={typographyStyle}>
                        Join a Team
                    </Typography>
                    {content.properties.teams.map((team) => (
                        <Box sx={alignHorizontalyBoxStyle}>
                            <Typography
                                variant="h6"
                                sx={typographyStyle}
                                onClick={() => navigate("/courses/" + courseId + "/classrooms/" + classroomId + "/assignments/" + assignment.id + "/teams/" + team.team.id)}
                            >
                                {team.team.name}
                            </Typography>
                            <Accordion
                                square={false}
                                sx={accordionStyle}
                            >
                                <AccordionSummary
                                    expandIcon={<ExpandMoreIcon />}
                                >
                                    <Typography
                                        variant="subtitle2"
                                        sx={typographyStyle}
                                    >
                                        Elements
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
                                        {team.students.map((student) => (
                                            <ListItem>
                                                <Typography
                                                    variant="subtitle2"
                                                    sx={typographyStyle}
                                                >
                                                    {student.name}
                                                </Typography>
                                            </ListItem>
                                        ))}
                                    </List>
                                </AccordionDetails>
                            </Accordion>
                            {team.team.isCreated ? (
                                    <>
                                        {team.team.isClosed?
                                            <Button variant="contained" onClick={handleJoinTeam} value={team.team.id} disabled={true}>Join</Button>
                                            :
                                            <Button variant="contained" onClick={handleJoinTeam} value={team.team.id} disabled={team.students.length >= assignment.maxElemsPerGroup}>Join</Button>
                                        }
                                    </>
                                ) :
                                <Button variant="contained" onClick={handleJoinTeam} value={team.team.id} disabled={true}>Being Created..</Button>
                            }
                        </Box>
                    ))}
                    </Grid>
                    <Grid item md={1} xs={3}>
                        <Typography variant="h4" sx={typographyStyle}>
                            Or
                        </Typography>
                    </Grid>
                    <Grid item md={5} xs={10}>
                        <Typography variant="h4" sx={typographyStyle}>
                            Create a Team
                        </Typography>
                        {content.properties.teams.length < assignment.maxNumberGroups ? (
                           <Button variant="contained" onClick={handleCreateTeam}>Create</Button>
                        ): (
                            <Typography variant="h6" sx={typographyStyle}>
                                You can't create a team, max number of teams reached
                            </Typography>
                        )}
                    </Grid>
                </Grid>
                </>
            ):null}
            <ErrorAlert error={serror} onClose={() => setError(null)}/>
        </Box>
    )
}
