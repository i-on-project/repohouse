package isel.ps.classcode.presentation.bootUp

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
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
fun BootUpScreen(actionHandler: () -> Unit = { }) {
    var visible by remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(Unit) {
            delay(timeMillis = BOOT_UP_DELAY)
            visible = false
            actionHandler()
        }
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            enter = slideInHorizontally() + fadeIn(animationSpec = tween(durationMillis = 1000, easing = LinearEasing)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000, easing = LinearEasing))
        ) {
            IselLogoView()
        }
    }
}

@Composable
fun IselLogoView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        @DrawableRes val logo = if (isSystemInDarkTheme()) R.drawable.logo_isel_white else R.drawable.logo_isel_black
        Image(
            painter = painterResource(id = logo),
            contentDescription = stringResource(id = R.string.logo),
            modifier = Modifier.size(200.dp)
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