import * as React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useLoggedIn } from './Auth'


export function RequireAuth({ children }: { children: React.ReactNode }): React.ReactElement {
    const currentUser = useLoggedIn()
    console.log("Current - " + currentUser)
    const location = useLocation()
    if (currentUser) {
        return <>{children}</>
    } else {
        return <Navigate to="/" state={{ source: location.pathname }} replace={ true }/>
    }
}
