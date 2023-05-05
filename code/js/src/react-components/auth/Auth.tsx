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
    githubId: number,
    setGithubId: (v: number) => void,
    userId: number,
    setUserId: (v: number) => void,
}

const LoggedInContext = createContext<ContextType>({
    loggedin: AuthState.None,
    setLogin: () => {},
    githubId: null,
    setGithubId: () => {},
    userId: null,
    setUserId: () => {},
})

export function AuthnContainer({ authState,githubId,userId, children }: { authState: AuthState,githubId:number,userId:number, children: React.ReactNode }): React.ReactElement {
    const [loggedin, setLoggedin] = useState(authState)
    const [git, setGit] = useState(githubId)
    const [user, setUser] = useState(userId)

    return (
        <LoggedInContext.Provider value={{loggedin: loggedin, setLogin: setLoggedin, githubId: git, setGithubId: setGit, userId: user, setUserId: setUser}}>
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

export function useGithubId() {
    return useContext(LoggedInContext).githubId
}

export function useSetGithubId() {
    return useContext(LoggedInContext).setGithubId
}

export function useUserId() {
    return useContext(LoggedInContext).userId
}

export function useSetUserId() {
    return useContext(LoggedInContext).setUserId
}
