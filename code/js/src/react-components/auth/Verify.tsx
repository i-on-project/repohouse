import * as React from "react"
import { useCallback, useState } from "react"
import { ErrorMessageModel } from "../../domain/response-models/Error"
import {Box, TextField, Typography,Button } from "@mui/material"
import { Navigate } from "react-router-dom"
import { AuthServices } from "../../services/AuthServices"
import { ErrorAlertForm } from "../error/ErrorAlert"
import { OTPBody } from "../../domain/dto/PendingUserDtoProperties"
import { SirenEntity } from "../../http/Siren"
import {alignHorizontalyBoxStyle, homeBoxStyle, typographyStyle} from "../../utils/Style";

export function ShowVerifyFetch({
    authServices,
}: {
    authServices: AuthServices
}) {

    if (window.opener) {
        window.opener.postMessage({type:"Auth", data:'/auth/verify'}, location.origin)
        window.close()
    }

    const [otp, setOtp] = useState<number>(null)
    const [serror, setError] = useState<ErrorMessageModel>(null)
    const [redirect,setRedirect] = useState(false)

    const handleSubmit = useCallback(async (event: any) => {
        event.preventDefault()
        if (otp != null && otp > 0) {
            const otpBody = new OTPBody(otp)
            const res = await authServices.verify(otpBody)
            if (res instanceof ErrorMessageModel) {
                setError(res)
            }
            if (res instanceof SirenEntity) {
                setRedirect(true)
            }
        }
    },[otp,setError,setRedirect])

    const handleResend = useCallback(async (event: any) => {
        event.preventDefault()
        const res = await authServices.resend()
        if (res instanceof ErrorMessageModel) {
            setError(res)
        }
    },[setError])


    if (redirect) {
        return <Navigate to={"/auth/student"}/>
    }

    return (
        <Box sx={homeBoxStyle}>
            <Typography
                variant="h6"
                gutterBottom
                sx={typographyStyle}
            >
                Verify
            </Typography>
            <TextField label={"OTP"} required type={"number"} onChange={(event) => setOtp(Number(event.target.value))}/>
            <Box sx={alignHorizontalyBoxStyle}>
                <Button variant="contained" onClick={handleSubmit}>Verify</Button>
                <Button variant="contained" onClick={handleResend}>Resend</Button>
            </Box>
            { serror ? <ErrorAlertForm title={serror.title} detail={serror.detail} onClose={() => { setError(null) }}/> : null }
        </Box>
    )
}



