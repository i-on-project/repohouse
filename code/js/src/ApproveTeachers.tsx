import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useCallback, useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import {List, ListItem, Typography} from "@mui/material";
import {MenuServices} from "./services/MenuServices";
import {Button} from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { Checkbox } from '@mui/material';

export function ShowTeacherApprovalFetch({
    menuServices,
}: {
    menuServices: MenuServices
}) {
    const content = useAsync(async () => {
        return await menuServices.getTeachersPendingApproval()
    });
    const [error, setError] = useState<ErrorMessageModel>(null)
    const [teachersApproved, setTeachersApproved] = useState<number[]>([])
    const [teachersRejected, setTeachersRejected] = useState<number[]>([])
    const navigate = useNavigate()
    // TODO: Find a way to change active button

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
            setError(r)
        } else {
            navigate("/menu")
        }
    }, [teachersApproved, teachersRejected])

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
                        {"Teachers Apply Requests"}
                    </Typography>
                    <Typography
                        variant="h4"
                    >
                        {"Approve, do Nothing or Reject"}
                    </Typography>
                    <List>
                        {content.properties.teachers.map((teacher) => (
                            <ListItem
                                key={teacher.id}
                            >
                                {teacher.name} - {teacher.email}
                               <HandleTeachersCheckbox value={teacher.id} acceptHandler={handleApprove} rejectHandler={handleReject} nothingHandler={handleNothing}/>
                            </ListItem>
                        ))}
                    </List>
                    { teachersApproved.length || teachersRejected.length ? <Button onClick={handleSubmit}> Submit </Button> : null}
                </>
            ) : null}
        </div>
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
        <Checkbox checked={isAcceptChecked} value={value} color={"success"} onChange={() => {
            setAcceptChecked(true)
            setRejectChecked(false)
            setNothingChecked(false)
            acceptHandler(value)
        }}/>
        <Checkbox checked={isNothingChecked} value={value} color={"info"} onChange={() => {
            setAcceptChecked(false)
            setRejectChecked(false)
            setNothingChecked(true)
            nothingHandler(value)
        }}/>
        <Checkbox checked={isRejectChecked} value={value} color={"error"} onChange={() => {
            setAcceptChecked(false)
            setRejectChecked(true)
            setNothingChecked(false)
            rejectHandler(value)
        }}/>
    </>
}
