import * as React from "react"
import { ErrorMessageModel } from "../../domain/response-models/Error"
import { Typography } from "@mui/material"

export function Error({ error }: { error: ErrorMessageModel }) {
    <>
        <Typography variant="h2">
            {error.title}
        </Typography>
        <Typography variant="h6" gutterBottom>
            {error.detail}
        </Typography>
    </>
}