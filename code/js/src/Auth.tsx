import * as React from 'react'
import {
    useState,
    createContext,
    useContext,
} from 'react'

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
    loggedin: AuthState | undefined,
    userId: number,
    setLogin: (v: AuthState) => void,
    setUserId: (v: number) => void
}

const LoggedInContext = createContext<ContextType>({
    loggedin: undefined,
    userId: 0,
    setLogin: () => {},
    setUserId: () => {}
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [loggedin, setLoggedin] = useState(undefined)
    const [userId, setUserId] = useState(0)

    return (
        <LoggedInContext.Provider value={{loggedin: loggedin, userId:userId,setLogin: setLoggedin, setUserId:setUserId}}>
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

export function useUserId() {
    return useContext(LoggedInContext).userId
}

export function useSetUserId() {
    return useContext(LoggedInContext).setUserId
}
