import * as React from "react"
import { Typography } from "@mui/material"

export function Error({ title, detail }: { title: string, detail: string }) {
    return <>
        <Typography variant="h2">
            {title}
        </Typography>
        <Typography variant="h6" gutterBottom>
            {detail}
        </Typography>
    </>
}
