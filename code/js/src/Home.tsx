import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useCallback, useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import { SystemServices } from "./services/SystemServices";
import {Typography} from "@mui/material";
import {Button} from "react-bootstrap";
import {useNavigate} from "react-router-dom";

export function ShowHomeFetch({
  systemServices,
}: {
    systemServices: SystemServices;
}) {
    const content = useAsync(async () => {
        return await systemServices.home();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const navigate = useNavigate();

    const handleAuthTeacherClick = useCallback(() => {
        navigate("/auth/teacher")
    }, [navigate])

    const handleAuthStudentClick = useCallback (() => {
        navigate("/auth/student")
    }, [navigate])

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
                        {content.properties.title}
                    </Typography>
                    <Typography
                        variant="h5"
                    >
                        {content.properties.description}
                    </Typography>
                    <Typography
                        variant="h6"
                    >
                        {"est: "+ content.properties.est}
                    </Typography>
                    <Button onClick={handleAuthTeacherClick}> {"Teacher"} </Button>
                    <Button onClick={handleAuthStudentClick}> {"Student"} </Button>
                </>
            ) : null}
        </div>
    );
}