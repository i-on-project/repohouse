package isel.ps.classcode.presentation.credits

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import isel.ps.classcode.R
import isel.ps.classcode.domain.Credits
import isel.ps.classcode.domain.CreditsStudent
import isel.ps.classcode.domain.CreditsTeacher
import isel.ps.classcode.presentation.views.TopBar

@Composable
fun ShowStudent(student: CreditsStudent, onSendEmailRequested: (String) -> Unit = { }, onOpenUrlRequested: (Uri) -> Unit = { }) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(modifier = Modifier.weight(0.75f), text = "${student.name} - ${student.schoolNumber}", style = MaterialTheme.typography.bodyMedium)
            Column(modifier = Modifier.weight(0.25f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
                IconButton(onClick = { onSendEmailRequested(student.email) }) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = stringResource(id = R.string.email_icon),
                    )
                }
                @DrawableRes val githubImage = if (isSystemInDarkTheme()) R.drawable.github_mark_white else R.drawable.github_mark
                Image(modifier = Modifier.size(20.dp).clickable { onOpenUrlRequested(Uri.parse(student.githubLink)) }, painter = painterResource(id = githubImage), contentDescription = stringResource(id = R.string.logo))
            }
        }
    }
}

@Composable
fun ShowTeacher(teacher: CreditsTeacher, onSendEmailRequested: (String) -> Unit = { }, onOpenUrlRequested: (Uri) -> Unit = { }) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(modifier = Modifier.weight(0.75f), text = teacher.name, style = MaterialTheme.typography.bodyMedium)
            Column(modifier = Modifier.weight(0.25f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
                IconButton(onClick = { onSendEmailRequested(teacher.email) }) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = stringResource(id = R.string.email_icon),
                    )
                }
                @DrawableRes val githubImage = if (isSystemInDarkTheme()) R.drawable.github_mark_white else R.drawable.github_mark
                Image(modifier = Modifier.size(20.dp).clickable { onOpenUrlRequested(Uri.parse(teacher.githubLink)) }, painter = painterResource(id = githubImage), contentDescription = stringResource(id = R.string.logo))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    credits: Credits = Credits(),
    onBackRequested: () -> Unit = { },
    onSendEmailRequested: (String) -> Unit = { },
    onOpenUrlRequested: (Uri) -> Unit = { },
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        topBar = { TopBar(name = stringResource(id = R.string.login), onBackRequested = onBackRequested) },
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(text = "Credits", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.size(8.dp))
            }
            credits.students.forEach {
                item { ShowStudent(student = it, onSendEmailRequested = onSendEmailRequested, onOpenUrlRequested = onOpenUrlRequested) }
            }
            item { ShowTeacher(teacher = credits.teacher, onSendEmailRequested = onSendEmailRequested, onOpenUrlRequested = onOpenUrlRequested) }
        }
    }
}
