import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useCallback, useEffect, useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import { SystemServices } from "./services/SystemServices";
import {List, ListItem, Typography} from "@mui/material";
import {MenuServices} from "./services/MenuServices";
import {MenuStudentDtoProperties, MenuTeacherDtoProperties} from "./domain/dto/MenuDtoProperties";
import {Link, Navigate} from "react-router-dom";
import {ErrorAlert} from "./ErrorAlert";
import {useLoggedIn, useSetLogin} from "./Auth";
import {OTPBody} from "./domain/dto/PendingUserDtoProperties";

export function ShowMenuFetch({
                                  menuServices,
                              }: {
    menuServices: MenuServices;
}) {

    const content = useAsync(async () => {
        return await menuServices.menu();
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
                    {content.properties instanceof MenuTeacherDtoProperties ? (
                        <>
                            <Link to={"/courses/create"}>{"Create Course"}</Link>
                            <Link to={"/pending-teachers"}>{"Pending Teachers"}</Link>
                        </>
                    ) : null}
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => { setError(null) }}/>
            // TODO: If teacher, add create course button
        </div>
    );
}

export function ShowMenuCallbackFetch(){
    window.opener.postMessage({type:"Menu", data:'/menu'},'http://localhost:3000/')
    window.close()
    return (<></>)
}