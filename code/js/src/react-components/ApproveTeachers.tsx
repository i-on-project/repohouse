import * as React from "react"
import { useAsync } from "../http/Fetch"
import { useCallback, useState } from "react"
import { ErrorMessageModel } from "../domain/response-models/Error"
import { SirenEntity } from "../http/Siren"
import {Backdrop, Box, Button, CircularProgress, List, ListItem, Typography} from "@mui/material"
import { MenuServices } from "../services/MenuServices"
import { useNavigate } from "react-router-dom"
import { Checkbox } from '@mui/material'
import { Check, Close, Remove } from "@mui/icons-material"
import { Error } from "./error/Error"
import {homeBoxStyle, typographyStyle} from "../utils/Style";

export function ShowTeacherApprovalFetch({
    menuServices,
}: {
    menuServices: MenuServices
}) {
    const content = useAsync(async () => {
        return await menuServices.getTeachersPendingApproval()
    })
    const [error, setError] = useState(false)
    const [teachersApproved, setTeachersApproved] = useState<number[]>([])
    const [teachersRejected, setTeachersRejected] = useState<number[]>([])
    const navigate = useNavigate()

    const handleApprove = useCallback((teacherId: number) => {
        if (teachersRejected.includes(teacherId)) {
            setTeachersRejected((teachersRejected.filter(id => id !== teacherId)))
        }
        setTeachersApproved([...teachersApproved, teacherId])
    }, [teachersApproved, teachersRejected])

    const handleReject = useCallback((teacherId: number) => {
        if (teachersApproved.includes(teacherId)) {
            setTeachersApproved((teachersApproved.filter(id => id !== teacherId)))
        }
        setTeachersRejected([...teachersRejected, teacherId])
    }, [teachersApproved, teachersRejected])

    const handleNothing = useCallback((teacherId: number) => {
        if (teachersApproved.includes(teacherId)) {
            setTeachersApproved((teachersApproved.filter(id => id !== teacherId)))
        }
        if (teachersRejected.includes(teacherId)) {
            setTeachersRejected((teachersRejected.filter(id => id !== teacherId)))
        }
    }, [teachersApproved, teachersRejected])

    const handleSubmit = useCallback(async (event:any) => {
        event.preventDefault()
        const r = await menuServices.approveTeacher(teachersApproved, teachersRejected)
        if (r instanceof ErrorMessageModel) {
            setError(true)
        } else {
            navigate("/menu")
        }
    }, [teachersApproved, teachersRejected])

    if (content instanceof ErrorMessageModel || error) {
        return <Error title="Communication with the server has failed" detail="Please try again."/>
    }
    
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

    return (
        <Box sx={homeBoxStyle}>
            {content instanceof SirenEntity ? (
                <>
                    <Typography
                        variant="h2"
                        sx={typographyStyle}
                    >
                        {"Teachers Apply Requests"}
                    </Typography>
                    <Typography
                        variant="h4"
                        sx={typographyStyle}
                    >
                        {"Approve, do Nothing or Reject"}
                    </Typography>
                    { content.properties.teachers.length !== 0 ?
                        <List>
                            {content.properties.teachers.map(teacher=> (
                                <ListItem
                                    key={teacher.id}
                                >
                                    <Typography variant="inherit" sx={typographyStyle}>
                                        {teacher.name + " (" + teacher.email +")"}
                                    </Typography>
                                <HandleTeachersCheckbox value={teacher.id} acceptHandler={handleApprove} rejectHandler={handleReject} nothingHandler={handleNothing}/>
                                </ListItem>
                            ))}
                        </List> :
                        <Typography variant="h6" sx={typographyStyle}>
                            {"There are no pending requests"}
                        </Typography>
                    }
                    { teachersApproved.length || teachersRejected.length ? <Button onClick={handleSubmit}> Submit </Button> : null}
                </>
            ) : null}
        </Box>
    );
}

function HandleTeachersCheckbox({ value, acceptHandler, rejectHandler, nothingHandler } : 
    {
        value : number, 
        acceptHandler: (teacherId: number) => void, 
        rejectHandler: (teacherId: number) => void,
        nothingHandler: (teacherId: number) => void
    }
) {
    const [isAcceptChecked, setAcceptChecked] = useState(false)
    const [isRejectChecked, setRejectChecked] = useState(false)
    const [isNothingChecked, setNothingChecked] = useState(true)   
    return <>
        <Checkbox checked={isAcceptChecked} value={value} icon={<Check/>} checkedIcon={<Check />} color={"success"} onChange={() => {
            setAcceptChecked(true)
            setRejectChecked(false)
            setNothingChecked(false)
            acceptHandler(value)
        }}/>
        <Checkbox checked={isNothingChecked} value={value} icon={<Remove/>} checkedIcon={<Remove />} color={"default"} onChange={() => {
            setAcceptChecked(false)
            setRejectChecked(false)
            setNothingChecked(true)
            nothingHandler(value)
        }}/>
        <Checkbox checked={isRejectChecked} value={value} icon={<Close />} checkedIcon={<Close />} color={"error"} onChange={() => {
            setAcceptChecked(false)
            setRejectChecked(true)
            setNothingChecked(false)
            rejectHandler(value)
        }}/>
    </>
}
