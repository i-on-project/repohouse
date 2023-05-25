import * as React from 'react'
import { ErrorMessageModel } from "../../domain/response-models/Error"


export function ErrorAlert({ error, onClose }: { error: ErrorMessageModel | null, onClose: () => void}) {

    if (error) return (
        <div style={{justifyContent:"center", alignContent: "center"}}>
            {error.title}
            {error.detail}
        </div>
    )
    return null
}

export function ErrorAlertForm({ title, detail, onClose }: { title: string, detail: string, onClose: () => void}) {
        return <div style={{justifyContent:"center", alignContent: "center"}}>
            {title}
            {detail}
        </div>
}
