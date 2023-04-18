import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useCallback, useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import { SystemServices } from "./services/SystemServices";
import {List, ListItem, Typography} from "@mui/material";
import {MenuServices} from "./services/MenuServices";
import {MenuStudentDtoProperties, MenuTeacherDtoProperties} from "./domain/dto/MenuDtoProperties";
import {Link} from "react-router-dom";
import {ErrorAlert} from "./ErrorAlert";
import {useSetLogin} from "./Auth";

export function ShowMenuFetch({
                                  menuServices,
                              }: {
    menuServices: MenuServices;
}) {
    const setLogin = useSetLogin();

    useCallback(() => {
        if (window.opener) {
            setLogin(true) // TODO: fix this
            window.opener.postMessage({type:"Auth", data:'/menu'},'http://localhost:3000/')
            window.close()
        }
    }, [setLogin, window.opener])

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
        </div>
    );
}