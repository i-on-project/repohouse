package isel.ps.classcode.presentation.course

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import isel.ps.classcode.R
import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.domain.Course
import isel.ps.classcode.presentation.login.LoginScreen
import isel.ps.classcode.presentation.views.AvatarImage
import isel.ps.classcode.presentation.views.LoadingAnimationCircle
import isel.ps.classcode.presentation.views.TopBar
import isel.ps.classcode.ui.theme.ClasscodeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomScreen(
    course: Course,
    classrooms: List<Classroom>? = listOf(),
    onClassroomSelected: (Int) -> Unit,
    onBackRequest: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopBar(
                name = stringResource(id = R.string.menu_top_page),
                onBackRequested = onBackRequest,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it.calculateTopPadding())
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            ShowCourseInfo(course = course)
            if (classrooms != null) {
                ShowListOfClassrooms(classrooms = classrooms, onClassroomSelected = onClassroomSelected)
            } else {
                LoadingAnimationCircle()
            }
        }
    }
}

@Composable
fun ShowCourseInfo(
    modifier: Modifier = Modifier,
    course: Course,
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
        AvatarImage(avatarUrl = course.imageUrl)
        Text(text = course.name, style = MaterialTheme.typography.titleMedium, color = textColor)
    }
}

@Composable
fun ShowListOfClassrooms(modifier: Modifier = Modifier, classrooms: List<Classroom>, onClassroomSelected: (Int) -> Unit = {  }) {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        items(classrooms.size) { index ->
            ClassroomCard(classroom = classrooms[index], onClassroomSelected = onClassroomSelected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomCard(
    modifier: Modifier = Modifier,
    classroom: Classroom,
    onClassroomSelected: (Int) -> Unit = {  }
)
{
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium,
        onClick = { onClassroomSelected(classroom.id) }
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = classroom.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClassroomCardPreview() {
    ClasscodeTheme() {
        LoginScreen()
    }
}