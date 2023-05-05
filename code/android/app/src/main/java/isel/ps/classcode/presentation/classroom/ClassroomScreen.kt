package isel.ps.classcode.presentation.classroom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Inventory2
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import isel.ps.classcode.R
import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.domain.Team
import isel.ps.classcode.presentation.course.ChosenIcon
import isel.ps.classcode.presentation.views.LoadingAnimationCircle
import isel.ps.classcode.presentation.views.TopBar

private enum class ListFilter {
    CREATED, NOT_CREATED, NORMAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomScreen(
    classroom: Classroom,
    teams: List<Team>? = null,
    onTeamSelected: (Team) -> Unit,
    onBackRequest: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopBar(
                name = stringResource(id = R.string.classroom_top_page),
                onBackRequested = onBackRequest,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding(), start = 24.dp, end = 24.dp)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            ShowClassroom(classroom = classroom)
            if (teams != null) {
                ShowTeams(teams = teams, onTeamSelected = onTeamSelected)
            } else {
                LoadingAnimationCircle()
            }
        }
    }
}

@Composable
fun ShowClassroom(modifier: Modifier = Modifier, classroom: Classroom) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .padding(top = 8.dp)
    ) {
        if (classroom.isArchived)
            Icon(imageVector = Icons.Default.Inventory2, contentDescription = stringResource(id = R.string.archived_icon))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = classroom.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
            Text(text = "last sync: ${classroom.lastSync}", style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
fun ShowTeams(modifier: Modifier = Modifier, teams: List<Team>, onTeamSelected: (Team) -> Unit) {
    var type by remember { mutableStateOf(ListFilter.NORMAL) }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        ChooseListFilter(type = type, onTypeChange = { type = it })
        LazyColumn(
            contentPadding = PaddingValues(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val list = when (type) {
                ListFilter.CREATED -> teams.filter { it.isCreated }
                ListFilter.NOT_CREATED -> teams.filter { !it.isCreated }
                else -> teams
            }
            items(list.size) { index ->
                TeamCard(team = list[index], onTeamSelected = onTeamSelected)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamCard(
    modifier: Modifier = Modifier,
    team: Team,
    onTeamSelected: (Team) -> Unit = {  }
)
{
    val color = if (team.isCreated) MaterialTheme.colorScheme.surfaceVariant else Color.Gray
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = { onTeamSelected(team) },
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = team.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}


@Composable
private fun ChooseListFilter(type: ListFilter, onTypeChange: (ListFilter) -> Unit) {
    var expanded by remember { mutableStateOf<Boolean>(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.FilterAlt,
                contentDescription = stringResource(id = R.string.filter_icon)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.dropdown_menu_item_normal)) },
                onClick = { onTypeChange(ListFilter.NORMAL) },
                trailingIcon = { if (type == ListFilter.NORMAL) ChosenIcon() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.dropdown_menu_item_created)) },
                onClick = { onTypeChange(ListFilter.CREATED) },
                trailingIcon = { if (type == ListFilter.CREATED) ChosenIcon() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.dropdown_menu_item_not_created)) },
                onClick = { onTypeChange(ListFilter.NOT_CREATED) },
                trailingIcon = { if (type == ListFilter.NOT_CREATED) ChosenIcon() }
            )
        }
    }
}