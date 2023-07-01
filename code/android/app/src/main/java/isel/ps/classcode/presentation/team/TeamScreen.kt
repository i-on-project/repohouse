package isel.ps.classcode.presentation.team

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PendingActions
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
import androidx.compose.ui.unit.dp
import isel.ps.classcode.R
import isel.ps.classcode.domain.ArchiveRepo
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.JoinTeam
import isel.ps.classcode.domain.LeaveTeam
import isel.ps.classcode.domain.LeaveTeamWithRepoName
import isel.ps.classcode.domain.RequestsHistory
import isel.ps.classcode.domain.RequestsThatNeedApproval
import isel.ps.classcode.domain.Team
import isel.ps.classcode.domain.TeamRequests
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.classroom.ShowRequestsStates
import isel.ps.classcode.presentation.views.ClassCodeErrorView
import isel.ps.classcode.presentation.views.GithubErrorView
import isel.ps.classcode.presentation.views.LoadingAnimationCircle
import isel.ps.classcode.presentation.views.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    team: Team,
    requests: TeamRequests?,
    onBackRequest: () -> Unit,
    onJoinTeamAccepted: (JoinTeam) -> Unit,
    onJoinTeamRejected: (JoinTeam) -> Unit,
    onLeaveTeamAccepted: (LeaveTeamWithRepoName) -> Unit,
    onLeaveTeamRejected: (LeaveTeam) -> Unit,
    errorClassCode: HandleClassCodeResponseError? = null,
    errorGitHub: HandleGitHubResponseError? = null,
    onDismissRequest: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var seeRequestHistory by remember { mutableStateOf(false) }
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
            if (requests != null) {
                ShowTeam(team = team)
                IconButton(onClick = { seeRequestHistory = !seeRequestHistory }) {
                    if (seeRequestHistory) {
                        Icon(imageVector = Icons.Default.PendingActions, contentDescription = stringResource(R.string.pending_icon))
                    } else {
                        Icon(imageVector = Icons.Default.History, contentDescription = stringResource(R.string.history_icon))
                    }
                }
                if (seeRequestHistory) {
                    ShowRequestsHistory(requests = requests.requestsHistory)
                } else {
                    ShowNeedApprovalRequests(requests = requests.needApproval, onJoinTeamAccepted = onJoinTeamAccepted, onJoinTeamRejected = onJoinTeamRejected, onLeaveTeamAccepted = onLeaveTeamAccepted, onLeaveTeamRejected = onLeaveTeamRejected)
                }
            } else {
                LoadingAnimationCircle()
            }
        }
    }
}

@Composable
fun ShowRequestsHistory(requests: RequestsHistory) {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            ShowCreateTeamComposite(
                createTeamComposite = requests.createTeamComposite,
            )
        }
        requests.joinTeam.forEach { joinTeam ->
            item {
                ShowJoinTeamRequest(joinTeam = joinTeam)
            }
        }
        requests.leaveTeam.forEach { leaveTeam ->
            item {
                ShowLeaveTeamRequest(leaveTeam = leaveTeam)
            }
        }
        if (requests.archiveRepo != null) {
            item {
                ShowArchiveRepoRequest(archiveRepo = requests.archiveRepo)
            }
        }
    }
}

@Composable
fun ShowNeedApprovalRequests(requests: RequestsThatNeedApproval, onJoinTeamAccepted: (JoinTeam) -> Unit, onJoinTeamRejected: (JoinTeam) -> Unit, onLeaveTeamAccepted: (LeaveTeamWithRepoName) -> Unit, onLeaveTeamRejected: (LeaveTeam) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        requests.joinTeam.forEach {
            item {
                ShowJoinTeamRequest(joinTeam = it, isPending = true, onJoinTeamAccepted = onJoinTeamAccepted, onJoinTeamRejected = onJoinTeamRejected)
            }
        }
        requests.leaveTeam.forEach {
            item {
                ShowLeaveTeamRequest(leaveTeam = it, isPending = true, onLeaveTeamAccepted = onLeaveTeamAccepted, onLeaveTeamRejected = onLeaveTeamRejected)
            }
        }
    }
}

@Composable
private fun ShowCreateTeamComposite(
    createTeamComposite: CreateTeamComposite,
) {
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
    }
}

@Composable
fun ShowArchiveRepoRequest(archiveRepo: ArchiveRepo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(15.dp))
            .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(16.dp))
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.weight(0.85f), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Archive repo", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = "State: ${archiveRepo.state}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Normal)
            Text(text = "User id: ${archiveRepo.creator}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Normal)
        }
    }
}

@Composable
fun ShowJoinTeamRequest(joinTeam: JoinTeam, isPending: Boolean = false, onJoinTeamAccepted: (JoinTeam) -> Unit = {}, onJoinTeamRejected: (JoinTeam) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(15.dp))
            .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(16.dp))
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.weight(0.85f), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Join team", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = "State: ${joinTeam.state}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Normal)
            Text(text = "User id: ${joinTeam.creator}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Normal)
        }
        if (isPending) {
            Column(
                modifier = Modifier.weight(0.15f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column {
                    IconButton(onClick = {
                        onJoinTeamRejected(joinTeam)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.closed_icon),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = {
                        onJoinTeamAccepted(joinTeam)
                    }) {
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
fun ShowLeaveTeamRequest(leaveTeam: LeaveTeamWithRepoName, isPending: Boolean = false, onLeaveTeamAccepted: (LeaveTeamWithRepoName) -> Unit = {}, onLeaveTeamRejected: (LeaveTeam) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(15.dp))
            .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(16.dp))
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.weight(0.85f), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Leave team", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = "State: ${leaveTeam.leaveTeam.state}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Normal)
            Text(text = "User id: ${leaveTeam.leaveTeam.creator}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Normal)
        }
        if (isPending) {
            Column(
                modifier = Modifier.weight(0.15f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column {
                    IconButton(onClick = {
                        onLeaveTeamRejected(leaveTeam.leaveTeam)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.closed_icon),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = {
                        onLeaveTeamAccepted(leaveTeam)
                    }) {
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
fun ShowTeam(modifier: Modifier = Modifier, team: Team) {
    Box(
        modifier = modifier
            .padding(top = 8.dp),
    ) {
        Text(text = team.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
    }
}
