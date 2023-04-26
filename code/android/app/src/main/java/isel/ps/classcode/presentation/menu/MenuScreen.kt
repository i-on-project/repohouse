package isel.ps.classcode.presentation.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import isel.ps.classcode.R
import isel.ps.classcode.domain.Course
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.presentation.views.LoadingAnimationCircle
import isel.ps.classcode.presentation.views.TopBar
import isel.ps.classcode.ui.theme.ClasscodeTheme

private val borderWidth = 4.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    userInfo: UserInfo?,
    courses: List<Course>? = listOf(),
    onCourseSelected: (Course) -> Unit = {  },
    onBackRequest: () -> Unit = {  },
    onCreditsRequested: () -> Unit = {  },
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopBar(
                name = stringResource(id = R.string.menu_top_page),
                onBackRequested = onBackRequest,
                scrollBehavior = scrollBehavior,
                onCreditsRequested = onCreditsRequested
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
            if (userInfo != null) {
                ShowUserInfo(userInfo = userInfo)
            } else {
                LoadingAnimationCircle()
            }
            if (courses != null) {
                ShowListOfCourses(list = courses, onCourseSelected = onCourseSelected)
            } else {
                LoadingAnimationCircle()
            }
        }
    }
}
@Composable
fun ShowUserInfo(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
        AvatarImage(avatarUrl = userInfo.avatarUrl)
        Text(text = userInfo.name, style = MaterialTheme.typography.titleMedium, color = textColor)
    }

}

@Composable
fun AvatarImage(modifier: Modifier = Modifier, avatarUrl: String) {
    val color = if (isSystemInDarkTheme()) Color.White else Color.Black
    Image(
        painter =  rememberAsyncImagePainter(
            model = avatarUrl,
        ),
        contentDescription = stringResource(id = R.string.user_avatar),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(150.dp)
            .clip(CircleShape)
            .border(
                BorderStroke(borderWidth, color),
                CircleShape
            )
            .padding(borderWidth)
            .clip(CircleShape)
    )
}

@Composable
fun ShowListOfCourses(modifier: Modifier = Modifier, list: List<Course>, onCourseSelected: (Course) -> Unit = {  }) {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        items(list.size) { index ->
            CourseCard(course = list[index], onCourseSelected = onCourseSelected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseCard(
    modifier: Modifier = Modifier,
    course: Course,
    onCourseSelected: (Course) -> Unit = { }
)
{
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium,
        onClick = { onCourseSelected(course) }
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = course.imageUrl),
                contentDescription = stringResource(id = R.string.course_image),
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(percent = 10))
            )
            Text(text = course.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview
@Composable
fun AvatarImagePreview() {
    ClasscodeTheme() {
        AvatarImage(avatarUrl = "https://avatars.githubusercontent.com/u/73882045?v=4")
    }
}

@Preview
@Composable
fun ShowUserInfoPreview() {
    ClasscodeTheme() {
        ShowUserInfo(userInfo = UserInfo(login = "test", id = 123L,name = "Test", email = "email", avatarUrl = "https://avatars.githubusercontent.com/u/73882045?v=4" ))
    }
}

@Preview
@Composable
fun MenuScreenPreview() {
    val list = List(10) { Course(id = it, name = "AED-$it",imageUrl = "https://avatars.githubusercontent.com/u/6817318?s=200&v=4") }

    ClasscodeTheme() {
        MenuScreen(
            courses = list,
            userInfo = UserInfo(
                login = "test",
                id = 123L,
                name = "Test",
                email = "email",
                avatarUrl = "https://avatars.githubusercontent.com/u/73882045?v=4"
            )
        )
    }
}

@Preview
@Composable
fun CourseCardPreview() {
    ClasscodeTheme() {
        CourseCard(course = Course(id = 1, name = "AED",imageUrl = "https://avatars.githubusercontent.com/u/6817318?s=200&v=4"))
    }
}

@Preview
@Composable
fun ShowListOfCoursesPreview() {
    val list = List(10) { Course(id = it, name = "AED-$it",imageUrl = "https://avatars.githubusercontent.com/u/6817318?s=200&v=4") }
    ClasscodeTheme {
        ShowListOfCourses(list = list)
    }
}
