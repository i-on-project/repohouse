import * as React from "react";
import { useAsync } from "./siren/Fetch";
import {useCallback, useState} from "react";
import { ErrorMessageModel } from "./domain/response-models/Error";
import { SirenEntity } from "./siren/Siren";
import {TextField, Typography} from "@mui/material";
import {Button} from "react-bootstrap";
import {Navigate, useNavigate} from "react-router-dom";
import {AuthServices} from "./services/AuthServices";
import {Label} from "@mui/icons-material";

export function ShowVerifyFetch({
  authServices
}: {
    authServices: AuthServices;
}) {
    const navigate = useNavigate();
    const [otp, setOtp] = useState<number>(null);

    const handleSubmit = (event: any) => {
        event.preventDefault();
        if (otp == null) return
        ShowVerifyFetchPost({authServices, otp})
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
            <form onSubmit={handleSubmit}>
                <TextField
                    id="outlined-basic"
                    label="OTP"
                    variant="outlined"
                    type="number"
                    onChange={(event) => setOtp(Number(event.target.value))}
                />
                <Button
                    variant="primary"
                    type="submit"
                >
                    Confirm
                </Button>
            </form>
        </div>
    );
}

export function ShowVerifyFetchPost({
                                               authServices,
                                               otp
                                           }: {
    authServices: AuthServices;
    otp: number;
}) {
    const content = useAsync(async () => {
        return await authServices.verify(otp);
    });
    const [error, setError] = useState<ErrorMessageModel>(null);

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

    return <Navigate to={"/menu"}/>
}
