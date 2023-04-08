import * as React from 'react'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { systemServices } from './Dependecies'
import { ShowHomeFetch } from './Home'



const router = createBrowserRouter([
    {
        "path": "/",
        "element": <Home/>
    },
])

export function App() {
    return (
       <RouterProvider router={router}/>
    )
}
function Home() {
    return (
        <div>
            <ShowHomeFetch systemServices={systemServices}/>
        </div>
    )
}

