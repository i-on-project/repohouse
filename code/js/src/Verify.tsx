import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useCallback, useEffect, useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import {TextField, Typography} from "@mui/material";
import {Button} from "react-bootstrap";
import {Navigate} from "react-router-dom";
import {AuthServices} from "./services/AuthServices";
import {ErrorAlert} from "./ErrorAlert";
import {OTPBody} from "./domain/dto/PendingUserDtoProperties";
import {useSetLogin} from "./Auth";
import {SirenEntity} from "./siren/Siren";

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
    const [create, setCreate] = useState<boolean>(false);
    const [otp, setOtp] = useState<number>(null);
    const [serror, setError] = useState<ErrorMessageModel>(error);

    const handleSubmit = useCallback((event:any) => {
        event.preventDefault()
        if (otp != null && otp > 0) {
            setCreate(true)
        }
    },[setCreate,otp])

    if (create) {
        const otpBody = new OTPBody(otp)
        return <ShowVerifyFetchPost authServices={authServices} otp={otpBody}/>
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

export function ShowVerifyFetchPost({
                                   authServices,
                                   otp
                               }: {
    authServices: AuthServices;
    otp: OTPBody;
}) {
    const content = useAsync(async () => {
        return await authServices.verify(otp);
    });
    const [error, setError] = useState<ErrorMessageModel>(null);
    const setLogin = useSetLogin();

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

    if (content instanceof ErrorMessageModel) {
       return <ShowVerifyFetch authServices={authServices} error={content}/>
    }


    setLogin(true) //TODO fix this
    return <Navigate to={"/menu"} replace={true}/>
}