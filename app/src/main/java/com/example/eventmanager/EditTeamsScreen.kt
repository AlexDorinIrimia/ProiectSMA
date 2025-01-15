package com.example.eventmanager

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@SuppressLint("MutableCollectionMutableState")
@Composable
fun EditTeamScreen(
    team: Team,
    allUsers: List<User>,
    onTeamUpdated: (Events, Team) -> Unit,
    onBack: () -> Unit,
    event: Events,
    onTeamEdited: Any,
    eventUsers: MutableList<User>,
    modifier: Modifier,
) {
    var selectedLeader by remember { mutableStateOf(team.teamLeader) }
    val selectedMembers = remember { team.members.toMutableList() }
    var expanded by remember { mutableStateOf(false) }
    val selectedEventMembers = remember { mutableListOf<User>() }

    // Filter users who are not already in the event
    val availableUsersForLeader = allUsers.filter { !event.users.contains(it) || it == team.teamLeader }
    val availableUsersForMembers = allUsers.filter { !selectedMembers.contains(it) && event.users.contains(it)}

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Team: ${team.name}",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Change Team Leader
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Team Leader: ${selectedLeader.fullName ?: "No Leader"}")
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Change Team Leader",
                modifier = Modifier.clickable { expanded = !expanded }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableUsersForLeader.forEach { user ->
                    DropdownMenuItem(
                        text = { Text(text = user.fullName) },
                        onClick = {
                            selectedLeader = user
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Current Members
        Text(text = "Current Members:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(selectedMembers.toList()) { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = user.fullName)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        selectedMembers.remove(user)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Member"
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Add Members
        Text(text = "Add Members:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        val availableUsers = availableUsersForMembers.filter { !selectedMembers.contains(it) }
        if (availableUsers.isNotEmpty()) {
            LazyColumn {
                items(availableUsers) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedEventMembers.contains(user),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedEventMembers.add(user)
                                } else {
                                    selectedEventMembers.remove(user)
                                }
                            }
                        )
                        Text(text = user.fullName)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                selectedMembers.addAll(selectedEventMembers)
                selectedEventMembers.clear()
            }) {
                Text(text = "Add Selected Members")
            }
        } else {
            Text(text = "No users available to add.")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                val updatedTeam = team.copy(teamLeader = selectedLeader, members = selectedMembers.toList()
                    .toMutableList())
                onTeamUpdated(
                    event,
                    updatedTeam
                )
                onBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Back Button
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}