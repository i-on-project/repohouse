import * as React from "react"
import {Box, Typography } from "@mui/material"
import {homeBoxStyle, typographyStyle} from "../../utils/Style";

export function Error({ title, detail }: { title: string, detail: string }) {
    return <Box sx={homeBoxStyle}>
        <Typography variant="h2" sx={typographyStyle}>
            {title}
        </Typography>
        <Typography variant="h6" gutterBottom sx={typographyStyle}>
            {detail}
        </Typography>
    </Box>
}
