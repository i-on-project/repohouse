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
    overflow: 'hidden',
    whiteSpace: 'normal',
    height: "87vh",
    backgroundColor: mainTheme.palette.background.default,
    mt: 1,
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
