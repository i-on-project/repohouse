import { createTheme } from "@mui/material"


export const mainTheme = createTheme({
    palette: {
        primary: {
            main: '#efd8cc',
            light: 'rgb(242, 223, 214)',
            dark: '#e0c1af',
            contrastText:'#403e3e',

        },
        background: {
            default: 'rgba(255,255,255,0.97)',
            paper: '#ffefe0',
        }
    },
    typography: {
        fontFamily: [
            'Roboto',
            'sans-serif',
        ].join(','),
    }
})
