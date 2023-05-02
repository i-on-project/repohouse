import * as React from 'react'
import { useState, createContext, useContext } from 'react'

export enum AuthState {
    None,
    Student,
    Teacher
}

export function toState(state: string): AuthState {
    if (state === "student") return AuthState.Student
    if (state === "teacher") return AuthState.Teacher
}

type ContextType = {
    loggedin: AuthState,
    setLogin: (v: AuthState) => void,
}

const LoggedInContext = createContext<ContextType>({
    loggedin: AuthState.None,
    setLogin: () => {},
})

export function AuthnContainer({ authState, children }: { authState: AuthState, children: React.ReactNode }) {
    const [loggedin, setLoggedin] = useState(authState)

    return (
        <LoggedInContext.Provider value={{loggedin: loggedin, setLogin: setLoggedin}}>
            {children}
        </LoggedInContext.Provider>
    )
}

export function useLoggedIn() {
    return useContext(LoggedInContext).loggedin
}

export function useSetLogin() {
    return useContext(LoggedInContext).setLogin
}