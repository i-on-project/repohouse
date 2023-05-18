package isel.ps.classcode.presentation.views

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import isel.ps.classcode.R
import isel.ps.classcode.domain.ProblemJson
import isel.ps.classcode.domain.deserialization.ProblemJsonDeserialization
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.ui.theme.ClasscodeTheme

@Composable
fun ClassCodeErrorView(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = { },
    handleClassCodeResponseError: HandleClassCodeResponseError
) {
    Box(
        modifier = modifier
    ) {
        when(handleClassCodeResponseError) {
            is HandleClassCodeResponseError.FailToGetTheHeader -> {
                val error = handleClassCodeResponseError.error
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    confirmButton = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = stringResource(id = R.string.checked_icon))
                    } },
                    icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = stringResource(id = R.string.warning_icon)) },
                    title = { Text(text = "Error in the request to the server", ) },
                    text = { Text(text = error) },
                )       
            }
            is HandleClassCodeResponseError.FailDeserialize -> {
                val error = handleClassCodeResponseError.error
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    confirmButton = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = stringResource(id = R.string.checked_icon))
                        } },
                    icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = stringResource(id = R.string.warning_icon)) },
                    title = { Text(text = "Error in the the process of the response from the server") },
                    text = { Text(text = error) },
                )
            }

            is HandleClassCodeResponseError.FailRequest -> {
                val error = handleClassCodeResponseError.error
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    confirmButton = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = stringResource(id = R.string.checked_icon))
                        } },
                    icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = stringResource(id = R.string.warning_icon)) },
                    title = { Text(text = "Error in the request to the server") },
                    text = { Text(text = " ${error.type}: ${error.title} \n ${error.detail}") },
                )
            }

            is HandleClassCodeResponseError.LinkNotFound -> {
                val error = handleClassCodeResponseError.error
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    confirmButton = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = stringResource(id = R.string.checked_icon))
                        } },
                    icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = stringResource(id = R.string.warning_icon)) },
                    title = { Text(text = "Error in the the process of getting a link") },
                    text = { Text(text = error) },
                )
            }

            is HandleClassCodeResponseError.Fail -> {
                val error = handleClassCodeResponseError.error
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    confirmButton = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = stringResource(id = R.string.checked_icon))
                        } },
                    icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = stringResource(id = R.string.warning_icon)) },
                    title = { Text(text = "Something just failed") },
                    text = { Text(text = error) },
                )
            }
        }
    }
}

@Composable
fun GithubErrorView(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = { },
    handleClassCodeResponseError: HandleGitHubResponseError
) {
    Box(
        modifier = modifier
    ) {
        when(handleClassCodeResponseError) {
            is HandleGitHubResponseError.FailDeserialize -> {
                val error = handleClassCodeResponseError.error
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    confirmButton = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = stringResource(id = R.string.checked_icon))
                        } },
                    icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = stringResource(id = R.string.warning_icon)) },
                    title = { Text(text = "Error in the the process of the response from github") },
                    text = { Text(text = error) },
                )
            }

            is HandleGitHubResponseError.FailRequest -> {
                val error = handleClassCodeResponseError.error
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    confirmButton = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = stringResource(id = R.string.checked_icon))
                        } },
                    icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = stringResource(id = R.string.warning_icon)) },
                    title = { Text(text = "Error in the request to the github") },
                    text = { Text(text = error.message) },
                )
            }
        }
    }
}


@Preview
@Composable
fun ErrorViewPreview() {
    ClasscodeTheme {
        ClassCodeErrorView(
            handleClassCodeResponseError = HandleClassCodeResponseError.FailDeserialize(error = "Error serializing the response")
        )
    }
}

@Preview
@Composable
fun ErrorViewPreview1() {
    ClasscodeTheme {
        ClassCodeErrorView(
            handleClassCodeResponseError = HandleClassCodeResponseError.FailToGetTheHeader(error = "Error getting the header")
        )
    }
}

@Preview
@Composable
fun ErrorViewPreview2() {
    ClasscodeTheme {
        ClassCodeErrorView(
            handleClassCodeResponseError = HandleClassCodeResponseError.FailRequest(error = ProblemJson(type = "type", title = "title", detail = "detail"))
        )
    }
}