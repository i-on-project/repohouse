package isel.ps.classcode.presentation.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import isel.ps.classcode.R

private val borderWidth = 4.dp

@Composable
fun AvatarImage(modifier: Modifier = Modifier, avatarUrl: String) {
    val color = if (isSystemInDarkTheme()) Color.White else Color.Black
    Image(
        painter = rememberAsyncImagePainter(
            model = avatarUrl,
        ),
        contentDescription = stringResource(id = R.string.user_avatar),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(150.dp)
            .clip(CircleShape)
            .border(
                BorderStroke(borderWidth, color),
                CircleShape,
            )
            .padding(borderWidth)
            .clip(CircleShape),
    )
}
