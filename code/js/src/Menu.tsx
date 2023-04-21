import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import {List, ListItem, Typography} from "@mui/material";
import {MenuServices} from "./services/MenuServices";
import {Link, useParams} from "react-router-dom";
import {ErrorAlert} from "./ErrorAlert";
import {AuthState, toState, useLoggedIn} from "./Auth";

export function ShowMenuFetch({
    menuServices,
}: {
    menuServices: MenuServices;
}) {

    const content = useAsync(async () => {
        return await menuServices.menu()
    })
    const [error, setError] = useState<ErrorMessageModel>(null)
    const loggedIn = useLoggedIn()

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
                    <List>
                        {content.properties.courses.map( course => (
                            <ListItem>
                                <Link to={"/courses/" + course.id}>{course.name}</Link>
                                <List>
                                    Teachers:
                                    {course.teacher.map( teacher => (
                                        <ListItem>
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
        window.opener.postMessage({type:"Menu", data:'/menu', state: state}, 'http://localhost:3000/')
        window.close()
        return (<></>)
    }
   
    return <>
        It seems that your server redirect URL is not setup correctly!
    </>
}
