package isel.ps.classcode.presentation.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Groups3
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import isel.ps.classcode.R
import isel.ps.classcode.ui.theme.ClasscodeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    name: String,
    onBackRequested: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    onCreditsRequested: (() -> Unit)? = null,
) {
    TopAppBar(
        title = { Text(text = name, style = MaterialTheme.typography.titleLarge) },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
        ),
        actions = {
            if (onCreditsRequested != null) {
                IconButton(onClick = onCreditsRequested) {
                    Icon(
                        imageVector = Icons.Outlined.Groups3,
                        contentDescription = stringResource(R.string.credits_icon),
                    )
                }
            }
        },
        navigationIcon = {
            if (onBackRequested != null) {
                IconButton(
                    modifier = Modifier.testTag("BackButton"),
                    onClick = onBackRequested,
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(R.string.arrow_back_text))
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TopBarPreview() {
    ClasscodeTheme {
        TopBar(name = stringResource(R.string.login))
    }
}
