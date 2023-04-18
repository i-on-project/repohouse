import * as React from 'react'
import { ErrorMessageModel } from "./domain/response-models/Error";
import {Alert, ListItemText} from "@mui/material"


export function ErrorAlert({ error, onClose }: { error: ErrorMessageModel | null, onClose: () => void}) {

    if (error) return (
        <div style={{justifyContent:"center", alignContent: "center"}}>
            <Alert sx={{ width: 1/6, alignContent: "center", justifyContent: "center"}} variant="filled" severity="error" onClose={() => onClose()}>
                <ListItemText
                      primary={error.error}
                      secondary={error.message}
                />
            </Alert>
        </div>
    )
    return null
}