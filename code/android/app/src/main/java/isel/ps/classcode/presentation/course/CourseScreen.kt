package isel.ps.classcode.presentation.course

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.login.LoginScreen
import isel.ps.classcode.presentation.views.AvatarImage
import isel.ps.classcode.presentation.views.ClassCodeErrorView
import isel.ps.classcode.presentation.views.GithubErrorView
import isel.ps.classcode.presentation.views.LoadingAnimationCircle
import isel.ps.classcode.presentation.views.TopBar
import isel.ps.classcode.ui.theme.ClasscodeTheme

private enum class ListFilter {
    ARCHIVED, UNARCHIVED, NORMAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    course: Course,
    classrooms: List<Classroom>? = null,
    onClassroomSelected: (Classroom) -> Unit,
    onBackRequest: () -> Unit,
    errorClassCode: HandleClassCodeResponseError? = null,
    errorGitHub: HandleGitHubResponseError? = null,
    onDismissRequest: () -> Unit = {},
    onReloadRequest: () -> Unit = { },
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var enabled by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            TopBar(
                name = stringResource(id = R.string.course_top_page),
                onBackRequested = onBackRequest,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding(), start = 16.dp, end = 16.dp)
                .background(color = MaterialTheme.colorScheme.background),
        ) {
            if (errorClassCode != null) {
                ClassCodeErrorView(handleClassCodeResponseError = errorClassCode, onDismissRequest = onDismissRequest)
            }
            if (errorGitHub != null) {
                GithubErrorView(handleGitHubResponseError = errorGitHub, onDismissRequest = onDismissRequest)
            }
            ShowCourseInfo(course = course)
            Spacer(modifier = Modifier.size(8.dp))
            if (classrooms != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    ShowListOfClassrooms(
                        modifier = Modifier.weight(0.85f),
                        classrooms = classrooms,
                        onClassroomSelected = onClassroomSelected
                    )
                    Box(modifier = Modifier.fillMaxSize().weight(0.15f)) {
                        IconButton(onClick = {
                            enabled = false
                            onReloadRequest()
                            enabled = true
                        }, enabled = enabled, modifier = Modifier.align(Alignment.Center)) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "")
                        }
                    }
                }
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
        modifier = modifier.padding(8.dp),
    ) {
        val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
        AvatarImage(avatarUrl = "https://avatars.githubusercontent.com/u/${course.orgId}?s=200&v=4")
        Text(text = course.name, style = MaterialTheme.typography.titleMedium, color = textColor)
    }
}

@Composable
fun ShowListOfClassrooms(modifier: Modifier = Modifier, classrooms: List<Classroom>, onClassroomSelected: (Classroom) -> Unit = { }) {
    var type by remember { mutableStateOf(ListFilter.NORMAL) }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        ChooseListFilter(type = type, onTypeChange = { type = it })
        LazyColumn(
            contentPadding = PaddingValues(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (classrooms.isEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.no_classrooms_created_empty_text),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                val list = when (type) {
                    ListFilter.ARCHIVED -> classrooms.filter { it.isArchived }
                    ListFilter.UNARCHIVED -> classrooms.filter { !it.isArchived }
                    else -> classrooms
                }
                items(list.size) { index ->
                    ClassroomCard(
                        classroom = list[index],
                        onClassroomSelected = onClassroomSelected,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomCard(
    modifier: Modifier = Modifier,
    classroom: Classroom,
    onClassroomSelected: (Classroom) -> Unit = { },
) {
    val color = if (classroom.isArchived) Color.Gray else MaterialTheme.colorScheme.surfaceVariant
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color,
        ),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = { onClassroomSelected(classroom) },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Text(text = classroom.name, style = MaterialTheme.typography.titleMedium)
            Text(text = classroom.lastSync.toString(), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ChooseListFilter(type: ListFilter, onTypeChange: (ListFilter) -> Unit) {
    var expanded by remember { mutableStateOf<Boolean>(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd),
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.FilterAlt,
                contentDescription = stringResource(id = R.string.filter_icon),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.dropdown_menu_item_normal)) },
                onClick = { onTypeChange(ListFilter.NORMAL) },
                leadingIcon = { if (type == ListFilter.NORMAL) ChosenIcon() },
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.dropdown_menu_item_archived)) },
                onClick = { onTypeChange(ListFilter.ARCHIVED) },
                leadingIcon = { if (type == ListFilter.ARCHIVED) ChosenIcon() },
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.dropdown_menu_item_unarchived)) },
                onClick = { onTypeChange(ListFilter.UNARCHIVED) },
                leadingIcon = { if (type == ListFilter.UNARCHIVED) ChosenIcon() },
            )
        }
    }
}

@Composable
fun ChosenIcon() {
    Icon(imageVector = Icons.Default.Check, contentDescription = stringResource(id = R.string.checked_icon))
}

@Preview(showBackground = true)
@Composable
fun ClassroomCardPreview() {
    ClasscodeTheme() {
        LoginScreen()
    }
}
