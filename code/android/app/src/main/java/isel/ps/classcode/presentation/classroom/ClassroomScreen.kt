package isel.ps.classcode.presentation.classroom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.List
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import isel.ps.classcode.R
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.domain.CreateRepo
import isel.ps.classcode.domain.CreateTeam
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.JoinTeam
import isel.ps.classcode.domain.Team
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.course.ChosenIcon
import isel.ps.classcode.presentation.views.ClassCodeErrorView
import isel.ps.classcode.presentation.views.GithubErrorView
import isel.ps.classcode.presentation.views.LoadingAnimationCircle
import isel.ps.classcode.presentation.views.TopBar

private enum class TypeOfTeam {
    TEAMS_CREATED,
    TEAMS_NOT_CREATED,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomScreen(
    classroom: Classroom,
    teamsCreated: List<Team>? = null,
    createTeamComposite: List<CreateTeamComposite>? = null,
    onTeamSelected: (Team) -> Unit,
    onCreateTeamComposite: (CreateTeamComposite, Boolean, Assignment) -> Unit,
    assignments: List<Assignment>?,
    assignment: Assignment?,
    onAssignmentChange: (Assignment) -> Unit,
    onBackRequest: () -> Unit,
    errorClassCode: HandleClassCodeResponseError? = null,
    errorGitHub: HandleGitHubResponseError? = null,
    onDismissRequest: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var typeOfTeam by remember { mutableStateOf(TypeOfTeam.TEAMS_CREATED) }
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
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding(), start = 24.dp, end = 24.dp)
                .background(color = MaterialTheme.colorScheme.background),
        ) {
            if (errorClassCode != null) {
                ClassCodeErrorView(
                    handleClassCodeResponseError = errorClassCode,
                    onDismissRequest = onDismissRequest,
                )
            }
            if (errorGitHub != null) {
                GithubErrorView(
                    handleGitHubResponseError = errorGitHub,
                    onDismissRequest = onDismissRequest,
                )
            }
            if (assignments != null) {
                ShowClassroom(classroom = classroom)
                if (assignment != null) {
                    FilterButtons(
                        typeOfTeam = typeOfTeam,
                        onTypeOfTeamChange = { type -> typeOfTeam = type },
                        assignment = assignment,
                        assignments = assignments,
                        onAssignmentChange = onAssignmentChange,
                    )
                    if (typeOfTeam == TypeOfTeam.TEAMS_CREATED) {
                        if (teamsCreated != null) {
                            ShowTeams(
                                teamsCreated = teamsCreated,
                                onTeamSelected = onTeamSelected,
                            )
                        }
                    } else {
                        if (createTeamComposite != null) {
                            ShowCreateTeamComposite(
                                createTeamComposite = createTeamComposite,
                                assignment = assignment,
                                onCreateTeamComposite = onCreateTeamComposite,
                            )
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
private fun ShowCreateTeamComposite(
    createTeamComposite: List<CreateTeamComposite>,
    assignment: Assignment,
    onCreateTeamComposite: (CreateTeamComposite, Boolean, Assignment) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (createTeamComposite.isEmpty()) {
            item {
                Text(text = stringResource(id = R.string.create_team_composite_empty_text), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            createTeamComposite.forEach { createTeamComposite ->
                item {
                    CreateTeamCompositeCard(createTeamComposite = createTeamComposite, assignment = assignment, onCreateTeamComposite = onCreateTeamComposite)
                }
            }
        }
    }
}
enum class PendingState { NORMAL, LOADING }

@Composable
fun CreateTeamCompositeCard(createTeamComposite: CreateTeamComposite, assignment: Assignment, onCreateTeamComposite: (CreateTeamComposite, Boolean, Assignment) -> Unit) {
    var pending by remember { mutableStateOf(PendingState.NORMAL) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(15.dp))
            .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(16.dp))
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.weight(0.85f)) {
            Text(text = "${createTeamComposite.compositeState} - ${createTeamComposite.createTeam.teamName}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            ShowRequestsStates(createTeamState = createTeamComposite.createTeam.state, joinTeamState = createTeamComposite.joinTeam.state, createRepoState = createTeamComposite.createRepo.state)
        }

        Column(modifier = Modifier.weight(0.15f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            if (createTeamComposite.compositeState == "Not_Concluded") {
                IconButton(onClick = {
                    pending = PendingState.LOADING
                    onCreateTeamComposite(createTeamComposite, true, assignment)
                }, enabled = pending == PendingState.NORMAL) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(id = R.string.refreshed_icon),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Column {
                    IconButton(onClick = {
                        pending = PendingState.LOADING
                        onCreateTeamComposite(createTeamComposite, false, assignment)
                    }, enabled = pending == PendingState.NORMAL) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.closed_icon),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = {
                        pending = PendingState.LOADING
                        onCreateTeamComposite(createTeamComposite, true, assignment)
                    }, enabled = pending == PendingState.NORMAL) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = stringResource(id = R.string.checked_icon),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShowRequestsStates(createTeamState: String, joinTeamState: String, createRepoState: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Create team", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Normal, modifier = Modifier.weight(0.30f))
            if (createTeamState == "Accepted") {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(id = R.string.checked_icon),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(12.dp)
                        .weight(0.5f),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Add user to team", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Normal, modifier = Modifier.weight(0.30f))
            if (joinTeamState == "Accepted") {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(id = R.string.checked_icon),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(12.dp)
                        .weight(0.5f),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Create repo", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Normal, modifier = Modifier.weight(0.30f))
            if (createRepoState == "Accepted") {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(id = R.string.checked_icon),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(12.dp)
                        .weight(0.5f),
                )
            }
        }
    }
}

@Preview
@Composable
fun Aaaa() {
    val createTeamComposite = CreateTeamComposite(
        compositeState = "Accepted",
        createTeam = CreateTeam(
            teamId = 1,
            gitHubTeamId = null,
            teamName = "Team test",
            requestId = 1,
            creator = 1,
            state = "Accepted",
            composite = 1,
        ),
        joinTeam = JoinTeam(
            githubUsername = "user",
            requestId = 2,
            creator = 1,
            state = "Accepted",
            composite = 1,
            teamId = 1,
        ),
        createRepo = CreateRepo(
            repoId = 1,
            repoName = "Accepted",
            requestId = 3,
            creator = 1,
            state = "Accepted",
            composite = 1,
        ),
    )
    val assignment = Assignment(
        id = 1,
        classroomId = 1,
        description = "description",
        title = "title",
    )
    CreateTeamCompositeCard(createTeamComposite = createTeamComposite, assignment = assignment, onCreateTeamComposite = { _, _, _ -> })
}

@Composable
fun ShowTeams(teamsCreated: List<Team>, onTeamSelected: (Team) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (teamsCreated.isEmpty()) {
            item {
                Text(text = stringResource(id = R.string.no_teams_text), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            items(teamsCreated.size) { index ->
                TeamCreatedCard(team = teamsCreated[index], onTeamSelected = onTeamSelected)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamCreatedCard(
    modifier: Modifier = Modifier,
    team: Team,
    onTeamSelected: (Team) -> Unit = { },
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = { onTeamSelected(team) },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Text(text = team.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun FilterButtons(
    modifier: Modifier = Modifier,
    typeOfTeam: TypeOfTeam,
    onTypeOfTeamChange: (TypeOfTeam) -> Unit,
    assignment: Assignment,
    assignments: List<Assignment>,
    onAssignmentChange: (Assignment) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    ) {
        AssignmentChooser(currentAssignment = assignment, assignments = assignments, onAssignmentChange = onAssignmentChange)
        Spacer(modifier = Modifier.padding(start = 8.dp))
        TypeOfTeamChooser(typeOfTeam = typeOfTeam, onTypeOfTeamChange = onTypeOfTeamChange)
    }
}

@Composable
fun AssignmentChooser(modifier: Modifier = Modifier, currentAssignment: Assignment, assignments: List<Assignment>, onAssignmentChange: (Assignment) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = stringResource(id = R.string.assignment_icon),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            assignments.forEach { assignment ->
                DropdownMenuItem(
                    text = { Text(assignment.title) },
                    onClick = { onAssignmentChange(assignment) },
                    leadingIcon = { if (currentAssignment == assignment) ChosenIcon() },
                )
            }
        }
    }
}

@Composable
private fun TypeOfTeamChooser(modifier: Modifier = Modifier, typeOfTeam: TypeOfTeam, onTypeOfTeamChange: (TypeOfTeam) -> Unit) {
    Box(modifier = modifier) {
        IconButton(onClick = {
            val newTypeOfTeam = if (typeOfTeam == TypeOfTeam.TEAMS_CREATED) TypeOfTeam.TEAMS_NOT_CREATED else TypeOfTeam.TEAMS_CREATED
            onTypeOfTeamChange(newTypeOfTeam)
        }) {
            Icon(imageVector = Icons.Default.List, contentDescription = stringResource(R.string.assignment_icon))
        }
    }
}

@Composable
fun ShowClassroom(modifier: Modifier = Modifier, classroom: Classroom) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .padding(top = 8.dp),
    ) {
        if (classroom.isArchived) {
            Icon(imageVector = Icons.Default.Inventory2, contentDescription = stringResource(id = R.string.archived_icon))
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = classroom.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
            Text(text = "last sync: ${classroom.lastSync}", style = MaterialTheme.typography.titleSmall)
        }
    }
}
