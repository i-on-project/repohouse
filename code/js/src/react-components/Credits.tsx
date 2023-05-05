import * as React from "react"
import { useAsync } from "../http/Fetch"
import {useCallback, useState} from "react"
import { ErrorMessageModel } from "../domain/response-models/Error"
import { SirenEntity } from "../http/Siren"
import { SystemServices } from "../services/SystemServices"
import {Backdrop, Box, CircularProgress, Grid, IconButton, Typography} from "@mui/material"
import { ErrorAlert } from "./error/ErrorAlert"
import {creditsBoxStyle1, creditsBoxStyle2, homeBoxStyle, typographyStyle} from "../utils/Style"
import {CreditsStudent, CreditsTeacher} from "../domain/dto/CreditsDtoProperties";
import EmailOutlinedIcon from '@mui/icons-material/EmailOutlined';
import GitHubIcon from '@mui/icons-material/GitHub';

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
                    <Box sx={creditsBoxStyle1}>
                        <Typography variant="h4" sx={typographyStyle}>
                            Students
                        </Typography>
                        <Box display="grid" gridTemplateColumns="repeat(12, 1fr)" gap={2}>
                            {content.properties.students.map((student) => (
                                <Box gridColumn="span 4">
                                    <Student student={student}/>
                                </Box>
                            ))}
                        </Box>
                    </Box>
                    <Box sx={creditsBoxStyle2}>
                        <Typography variant="h4" sx={typographyStyle}>
                            Professor
                        </Typography>
                        <Box display="grid" gridTemplateColumns="repeat(12, 1fr)" gap={2}>
                            <Box gridColumn="span 12">
                                <Teacher teacher={content.properties.teacher}/>
                            </Box>
                        </Box>
                    </Box>

                </>
            ) : null}
            <ErrorAlert error={error} onClose={() => { setError(null) }}/>
        </Box>
    );
}

function Student ({ student }: { student: CreditsStudent }){

    const handleEmailClick = useCallback(() => {
        window.open(`mailto:${student.email}?subject=Contact - ClassCode`, '_blank')
    }, [student.email])

    const handleGithubClick = useCallback(() => {
        window.open(student.githubLink, '_blank')
    }, [student.githubLink])

    return (
        <Box>
            <Typography variant="h6" sx={typographyStyle}>
                {student.name}
            </Typography>
            <Typography variant="subtitle2" sx={typographyStyle}>
                {student.schoolNumber}
            </Typography>
            <IconButton>
                <EmailOutlinedIcon onClick={handleEmailClick}/>
            </IconButton>
            <IconButton>
                <GitHubIcon onClick={handleGithubClick}/>
            </IconButton>
        </Box>
    )
}

function Teacher ({ teacher }: { teacher: CreditsTeacher }){

    const handleEmailClick = useCallback(() => {
        window.open(`mailto:${teacher.email}?subject=Contact - ClassCode`, '_blank')
    }, [teacher.email])

    const handleGithubClick = useCallback(() => {
        window.open(teacher.githubLink, '_blank')
    }, [teacher.githubLink])

    return (
        <Box>
            <Typography variant="h6" sx={typographyStyle}>
                {teacher.name}
            </Typography>
            <IconButton>
                <EmailOutlinedIcon onClick={handleEmailClick}/>
            </IconButton>
            <IconButton>
                <GitHubIcon onClick={handleGithubClick}/>
            </IconButton>
        </Box>
    )
}

