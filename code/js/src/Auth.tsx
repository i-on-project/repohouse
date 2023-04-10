import * as React from 'react'
import {
    useState,
    createContext,
    useContext,
} from 'react'


type ContextType = {
    loggedin: boolean,
    userId: number,
    setLogin: (v: boolean) => void,
    setUserId: (v: number) => void
}

const LoggedInContext = createContext<ContextType>({
    loggedin: false,
    userId: 0,
    setLogin: () => {},
    setUserId: () => {}
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [loggedin, setLoggedin] = useState(false)
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
