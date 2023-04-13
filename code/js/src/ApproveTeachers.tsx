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
import {Button} from "react-bootstrap";

export function ShowPendingTeacherFetch({
                                  menuServices,
                              }: {
    menuServices: MenuServices;
}) {
    const content = useAsync(async () => {
        return await menuServices.getTeachersPendingApproval();
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const [teachersApproved, setTeachersApproved] = useState<number[]>([]);
    const [teachersRejected, setTeachersRejected] = useState<number[]>([]);
    // TODO: Find a way to change active button

    const handleApprove = useCallback((event:any) => {
        event.preventDefault()
        const teacherId = parseInt(event.currentTarget.value)
        if (teachersRejected.includes(teacherId)) {
            setTeachersRejected((teachersRejected.filter(id => id !== teacherId)))
        }
        setTeachersApproved([...teachersApproved, teacherId])
    }, [teachersApproved, teachersRejected,setTeachersRejected,setTeachersApproved])

    const handleReject = useCallback((event:any) => {
        event.preventDefault()
        const teacherId = parseInt(event.currentTarget.value)
        if (teachersApproved.includes(teacherId)) {
            setTeachersApproved((teachersApproved.filter(id => id !== teacherId)))
        }
        setTeachersRejected([...teachersRejected, teacherId])
    }, [teachersApproved, teachersRejected,setTeachersRejected,setTeachersApproved])

    const handleNothing = useCallback((event:any) => {
        event.preventDefault()
        const teacherId = parseInt(event.currentTarget.value)
        if (teachersApproved.includes(teacherId)) {
            setTeachersApproved((teachersApproved.filter(id => id !== teacherId)))
        }
        if (teachersRejected.includes(teacherId)) {
            setTeachersRejected((teachersRejected.filter(id => id !== teacherId)))
        }
    }, [teachersApproved, teachersRejected,setTeachersRejected,setTeachersApproved])

    const handleSubmit = useCallback((event:any) => {
        event.preventDefault()
        menuServices.approveTeacher(teachersApproved, teachersRejected).then(r => {
            if (r instanceof ErrorMessageModel) {
                setError(r)
            } else {
                window.location.reload()
            }
        })
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
                        {"Teachers"}
                    </Typography>
                    <List>
                        {content.properties.teacher.map((teacher) => (
                            <ListItem
                                key={teacher.id}
                            >
                                {teacher.name} - {teacher.email}
                                <Button onClick={handleApprove} value={teacher.id} active={false}> Approve </Button>
                                <Button onClick={handleNothing} value={teacher.id} active={true} > Nothing </Button>
                                <Button onClick={handleReject} value={teacher.id} active={false}> Reject </Button>
                            </ListItem>
                        ))}
                    </List>
                    <Button onClick={handleSubmit}> Submit </Button>
                </>
            ) : null}
        </div>
    );
}