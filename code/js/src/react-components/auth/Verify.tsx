import * as React from "react";
import {useCallback, useState} from "react";
import { ErrorMessageModel } from "../../domain/response-models/Error";
import {TextField, Typography} from "@mui/material";
import {Button} from "react-bootstrap";
import {Navigate} from "react-router-dom";
import {AuthServices} from "../../services/AuthServices";
import {ErrorAlert} from "../error/ErrorAlert";
import {OTPBody} from "../../domain/dto/PendingUserDtoProperties";
import {AuthState, useSetLogin} from "./Auth";
import {SirenEntity} from "../../http/Siren";

export function ShowVerifyFetch({
  authServices,
    error
}: {
    authServices: AuthServices;
    error: ErrorMessageModel;
}) {

    if (window.opener) {
        window.opener.postMessage({type:"Auth", data:'/auth/verify'},'http://localhost:3000/')
        window.close()
    }

    const [otp, setOtp] = useState<number>(null);
    const [serror, setError] = useState<ErrorMessageModel>(error);
    const setLogin = useSetLogin();
    const [redirect,setRedirect] = useState(false)

    const handleSubmit = useCallback(async (event: any) => {
        event.preventDefault()
        if (otp != null && otp > 0) {
            const otpBody = new OTPBody(otp)
            const res = await authServices.verify(otpBody);
            if (res instanceof ErrorMessageModel) {
                setError(res)
            }
            if (res instanceof SirenEntity) {
                setLogin(AuthState.Student)
                setRedirect(true)
            }
        }
    },[otp,setError,setLogin,setRedirect])

    if (redirect){
        return <Navigate to={"/menu"} replace={true}/>
    }

    return (
        <div
            style={{
                alignItems: "center",
                justifyContent: "space-evenly",
            }}
        >
            <Typography
                variant="h6"
                gutterBottom
            >
                Verify
            </Typography>
            <TextField label={"OTP"} required type={"number"} onChange={(event) => setOtp(Number(event.target.value))}/>
            <Button onClick={handleSubmit}>Send</Button>
            <ErrorAlert error={serror} onClose={() => { setError(null) }}/>
        </div>
    );
}
