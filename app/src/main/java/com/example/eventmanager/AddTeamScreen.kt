package com.example.eventmanager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddTeamScreen(
    event: Events,
    allUsers: List<User>,
    onTeamAdded: (Events, Team) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier
) {
    var teamName by remember { mutableStateOf("") }
    var selectedLeader by remember { mutableStateOf<User?>(null) }
    var expanded by remember { mutableStateOf(false) }

    // Filter users who are not already in the event
    val availableUsers = allUsers.filter { !event.users.contains(it) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Add New Team", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Team Name Input
        OutlinedTextField(
            value = teamName,
            onValueChange = { teamName = it },
            label = { Text("Team Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Select Team Leader
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Team Leader: ${selectedLeader?.fullName ?: "Select Leader"}")
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Select Team Leader"
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableUsers.forEach { user ->
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

        // Add Team Button
        Button(
            onClick = {
                if (teamName.isNotBlank() && selectedLeader != null) {
                    val newTeam = Team(teamName, selectedLeader!!)
                    onTeamAdded(event,newTeam)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = teamName.isNotBlank() && selectedLeader != null
        ) {
            Text("Add Team")
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