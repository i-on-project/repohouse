package isel.ps.classcode.presentation.bootUp

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import isel.ps.classcode.R
import isel.ps.classcode.ui.theme.ClasscodeTheme
import kotlinx.coroutines.delay

const val BOOT_UP_DELAY = 2000L

@Composable
fun BootUpScreen(strongBiometric: () -> Unit = { }) {
    var visible by remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(Unit) {
            delay(timeMillis = BOOT_UP_DELAY)
            strongBiometric()
            visible = false
        }
        AnimatedVisibility(
            visible = true,
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            enter = slideInHorizontally() + fadeIn(animationSpec = tween(durationMillis = 1000, easing = LinearEasing)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000, easing = LinearEasing)),
        ) {
            if (visible) {
                IselLogoView()
            } else {
                Screen(actionHandler = strongBiometric)
            }
        }
    }
}

@Composable
fun Screen(actionHandler: () -> Unit = { }) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = { actionHandler() }, modifier = Modifier.size(200.dp)) {
            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun IselLogoView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        @DrawableRes val logo = if (isSystemInDarkTheme()) R.drawable.logo_isel_white else R.drawable.logo_isel_black
        Image(
            painter = painterResource(id = logo),
            contentDescription = stringResource(id = R.string.logo),
            modifier = Modifier.size(200.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BootUpScreenLightPreview() {
    ClasscodeTheme() {
        BootUpScreen()
    }
}
