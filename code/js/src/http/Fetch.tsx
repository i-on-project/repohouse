import { useEffect, useState } from 'react'
import { ErrorMessageModel } from "../domain/response-models/Error"
import { SirenEntity } from './Siren'


export async function fetchGet<T>(url: string) {
    const options = {
        method:"GET"
    }
    return await myFetch<T>(url, options)
}

export async function fetchPut<T>(url: string, body:object =null) {
    const options = {
        method:"PUT",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body),
    }
    return myFetch<T>(url, options)
}

export async function fetchPost<T>(url: string, body:object =null) {
    const options = {
        method:"POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body),
    }
    return myFetch<T>(url, options)
}

export async function fetchDelete<T>(url: string) {
    const options = {
        method:"DELETE",
        headers: {
            'Content-Type': 'application/json'
        }
    }
    return myFetch<T>(url, options)
}

async function myFetch<T>(url: string, options: object) {
    const res = await fetch(url, options)
    const resp = await res.json()
    return handleResponse<T>(resp, res.ok)
}

export function useAsync<T>(callback: () => Promise<SirenEntity<T> | ErrorMessageModel>) : SirenEntity<T> | ErrorMessageModel | undefined {
    const [content, setContent] = useState(undefined)
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const res = await callback()
            if (!cancelled) {
                setContent(res)
            }
        }
        doFetch()
        return () => {
            cancelled = true
        }
    }, [setContent])

    return content
}

function handleResponse<T>(response: any, isOk:boolean): SirenEntity<T> | ErrorMessageModel {
    if (isOk) {
        return new SirenEntity<T>(response.cls, response.properties, response.actions, response.links)
    } else {
        return new ErrorMessageModel(response.type, response.title, response.detail)
    }
}
