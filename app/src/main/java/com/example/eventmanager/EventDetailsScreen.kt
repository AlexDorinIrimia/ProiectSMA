package com.example.eventmanager

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventDetailsScreen(
    event: Events,
    allEvents: List<Events>,
    onAnnouncementAdded: (Events, AnnouncementData) -> Unit,
    onTeamAdded: (Events, Team) -> Unit,
    onTeamUpdated: (Events, Team) -> Unit,
    onTeamRemoved: (Events, Team) -> Unit,
    modifier: Modifier = Modifier,
    onAddTeamClick: () -> Unit,
    onAddAnnouncementClick: () -> Unit
) {
    Log.d("EventDetailsScreen", "Event ${event.name} has ${event.teams.size} teams")
    event.teams.forEach { team ->
        Log.d("EventDetailsScreen", "Team name: ${team.name}")
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Navigate to AddAnnouncementScreen
            }) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Event Details", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Name: ${event.name}")
            Text(text = "Place: ${event.place}")
            Text(text = "Start Time: ${event.startTime.format(DateTimeFormatter.ofPattern("yyyy, MMMM, dd, HH:mm"))}")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Announcements", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
            LazyColumn {
                items(event.announcements) { announcement ->
                    Text(text = "Title: ${announcement.announcementData.title}")
                    Text(text = "Description: ${announcement.announcementData.description}")
                    Text(text = "Date: ${announcement.announcementData.date}")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Teams", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
            LazyColumn {
                items(event.teams) { team ->
                    Text(text = "Team Name: ${team.name}")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Navigate to AddTeamScreen
            }) {
                Text("Add Team")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Navigate to AddAnnouncementScreen
            }) {
                Text("Add Announcement")
            }
        }
    }
}