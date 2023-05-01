import * as React from "react"
import { useAsync } from "../http/Fetch"
import { useState } from "react"
import { ErrorMessageModel } from "../domain/response-models/Error"
import { SirenEntity } from "../http/Siren"
import { SystemServices } from "../services/SystemServices"
import { Typography } from "@mui/material"
import { ErrorAlert } from "./error/ErrorAlert"

export function ShowCreditsFetch({
    systemServices,
}: {
    systemServices: SystemServices;
}) {
    const content = useAsync(async () => {
        return await systemServices.credits()
    })
    const [error, setError] = useState<ErrorMessageModel>(null)

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
                        {"Credits"}
                    </Typography>
                    <Typography
                        variant="h5"
                    >
                        {"Teacher"}
                        <ul>
                            <li>
                                {content.properties.teacher.name}
                            </li>
                            <li>
                                {content.properties.teacher.email}
                            </li>
                        </ul>
                    </Typography>
                    <Typography
                        variant="h5"
                    >
                        {"Students"}
                        <ul>
                            {content.properties.students.map((student) => (
                                <li key={student.schoolNumber}>
                                    {student.name}
                                    <ul>
                                        <li>
                                            {student.email}
                                        </li>
                                        <li>
                                            {"School_Id: " + student.schoolNumber}
                                        </li>
                                    </ul>
                                </li>
                            ))}
                        </ul>
                    </Typography>
                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => { setError(null) }}/>
        </div>
    );
}