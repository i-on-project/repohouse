package isel.ps.classcode.presentation.login

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import isel.ps.classcode.R
import isel.ps.classcode.presentation.views.TopBar
import isel.ps.classcode.ui.theme.ClasscodeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(loginHandler: () -> Unit = {  }) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(name = stringResource(id = R.string.login)) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it.calculateTopPadding())
                .background(color = MaterialTheme.colorScheme.background)
            ,
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            @DrawableRes val logo = if (isSystemInDarkTheme()) R.drawable.github_mark_white else R.drawable.github_mark
            Image(
                painter = painterResource(id = logo),
                contentDescription = stringResource(id = R.string.logo),
                modifier = Modifier.size(200.dp)
            )
            OutlinedButton(
                onClick = {
                    loginHandler()
                },
            ) {
                Text(text = stringResource(id = R.string.login), style = MaterialTheme.typography.titleMedium,)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GithubLoginScreenPreview() {
    ClasscodeTheme() {
        LoginScreen()
    }
}