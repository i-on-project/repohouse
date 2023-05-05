import {mainTheme} from "./Theme";

export const typographyStyle = {
    fontFamily: mainTheme.typography.fontFamily,
    letterSpacing: '.3rem',
    color: mainTheme.palette.primary.contrastText,
    textDecoration: 'none',
    display:"block",
    noWrap: true,
    gutterBottom:true,
    align:"center",
    m:1
}
export const homeBoxStyle = {
    boxShadow: 1,
    display:"flex",
    justifyContent:"center",
    alignItems:"center",
    flexDirection:"column",
    minHeight: "87vh",
    maxWidth: "100%",
    backgroundColor: mainTheme.palette.background.default,
    mt: 10
}


export const creditsBoxStyle1 = {
    boxShadow: 1,
    borderRadius: 1,
    backgroundColor: mainTheme.palette.background.paper,
    display:"flex",
    justifyContent:"center",
    alignItems:"center",
    flexDirection:"column",
    overflow: 'hidden',
    whiteSpace: 'normal',
    mt:-10
}

export const creditsBoxStyle2 = {
    boxShadow: 1,
    borderRadius: 1,
    backgroundColor: mainTheme.palette.background.paper,
    display:"flex",
    justifyContent:"center",
    alignItems:"center",
    flexDirection:"column",
    overflow: 'hidden',
    whiteSpace: 'normal',
    mt:10
}

export const modalBoxStyle = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    borderBackground: 'white',
    boxShadow: 24,
    p: 4,
    display:"flex",
    justifyContent:"center",
    alignItems:"center",
    flexDirection:"column",
    overflow: 'hidden',
    whiteSpace: 'normal',
    width: {xs:"70%", md:"25%"},
    bgcolor: 'background.default',
    border: '2px solid #000',
    borderRadius: 1,
}

export const alignHorizontalyBoxStyle = {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    flexDirection: 'row',
    mt: 2,
    gap: 2,
}

export const cardBoxStyle = {
    border: 1,
    borderRadius: 1,
    borderColor: 'grey.500',
    p: 2,
    display:"flex",
    justifyContent:"center",
    alignItems:"center",
    flexDirection:"column",
    backgroundColor: mainTheme.palette.background.paper,
    '&:hover': {
        backgroundColor: mainTheme.palette.primary.dark,
        cursor: 'pointer',
    },
}

export const cardBoxStyle2 = {
    border: 1,
    borderRadius: 1,
    borderColor: 'grey.500',
    p: 1,
    display:"flex",
    justifyContent:"center",
    alignItems:"center",
    flexDirection:"column",
    backgroundColor: mainTheme.palette.secondary.main,
    '&:hover': {
        backgroundColor: mainTheme.palette.secondary.dark,
        cursor: 'pointer',
    },
}

export const accordionStyle = {
    display:"flex",
        justifyContent:"center",
        alignItems:"center",
        flexDirection:"column",
        boxShadow: "none",
        backgroundColor: mainTheme.palette.background.paper,
        '&:hover': {
        backgroundColor: mainTheme.palette.primary.dark,
            cursor: 'pointer',
    },
    borderRadius: 1,
        border: 1,
        borderColor: mainTheme.palette.primary.dark,
        borderStyle: "solid",
        mb: 4,
        maxWidth: {xs:"50%",md:"25%"}
}
