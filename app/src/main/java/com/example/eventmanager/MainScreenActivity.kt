package com.example.eventmanager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import com.example.eventmanager.ui.theme.EventManagerTheme

class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventManagerTheme {
                MainScreen()
            }
        }
    }
}

data class Event(
    val id: Int, // Unique identifier for the event
    val name: String, // Name or title of the event
    val description: String, // Detailed description of the event
    val startTime: LocalDateTime, // Date and time when the event starts
    val endTime: LocalDateTime, // Date and time when the event ends
    val location: String? = null, // Optional location of the event
    val organizer: String? = null, // Optional name of the event organizer
    val maxAttendees: Int? = null, // Optional maximum number of attendees
    val currentAttendees: Int = 0 // Current number of attendees (default to 0)
)

open class Screen {
    object Events : Screen()
    object AccountDetails : Screen()
    object EditAccountDetails : Screen()
    data class EventDetails(val event: Event) : Screen()
}

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen: Screen by remember { mutableStateOf(Screen.Events) }
    val events = remember {
        listOf(
            Event(
                id = 1,
                name = "Tech Conference 2024",
                description = "A conference for tech enthusiasts.",
                startTime = LocalDateTime.of(2024, 10, 26, 9, 0),
                endTime = LocalDateTime.of(2024, 10, 28, 17, 0),
                location = "Convention Center",
                maxAttendees = 500
            ),
            Event(
                id = 2,
                name = "Summer Music Festival",
                description = "A fun music festival in the park.",
                startTime = LocalDateTime.of(2024, 7, 15, 12, 0),
                endTime = LocalDateTime.of(2024, 7, 15, 22, 0),
                location = "Central Park",
                organizer = "Music Lovers Inc.",
                currentAttendees = 100
            )
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    onItemClick = { item ->
                        currentScreen = when (item) {
                            "Events" -> Screen.Events
                            "Account Details" -> Screen.AccountDetails
                            else -> Screen.Events
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        when (currentScreen) {
                            is Screen.Events -> Text("Events")
                            is Screen.AccountDetails -> Text("Account Details")
                            is Screen.EventDetails -> Text((currentScreen as Screen.EventDetails).event.name)
                            is Screen.EditAccountDetails -> Text("Edit Account Details")
                        }
                    },
                    navigationIcon = {
                        when (currentScreen) {
                            is Screen.Events, is Screen.AccountDetails -> {
                                IconButton(onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }) {
                                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                                }
                            }

                            is Screen.EventDetails -> {
                                IconButton(onClick = { currentScreen = Screen.Events }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }

                            is Screen.EditAccountDetails -> {
                                IconButton(onClick = { currentScreen = Screen.AccountDetails }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                when (currentScreen) {
                    is Screen.Events -> EventList(events) { event ->
                        currentScreen = Screen.EventDetails(event)
                    }

                    is Screen.AccountDetails -> AccountDetails {
                        currentScreen = Screen.EditAccountDetails
                    }

                    is Screen.EventDetails -> EventDetailsScreen((currentScreen as Screen.EventDetails).event)
                    is Screen.EditAccountDetails -> EditAccountDetails()
                }
            }
        }
    }
}

@Composable
fun EventDetailsScreen(event: Event) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = event.name, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Description:", style = MaterialTheme.typography.headlineSmall)
        Text(text = event.description)
        Spacer(modifier = Modifier.height(8.dp))

        if (event.location != null) {
            Text(text = "Location:", style = MaterialTheme.typography.headlineSmall)
            Text(text = event.location)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (event.organizer != null) {
            Text(text = "Organizer:", style = MaterialTheme.typography.headlineSmall)
            Text(text = event.organizer)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (event.maxAttendees != null) {
            Text(text = "Max Attendees:", style = MaterialTheme.typography.headlineSmall)
            Text(text = event.maxAttendees.toString())
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(text = "Current Attendees:", style = MaterialTheme.typography.headlineSmall)
        Text(text = event.currentAttendees.toString())
    }
}

@Composable
fun EditAccountDetails() {
    TODO("Not yet implemented")
}

@Composable
fun DrawerContent(onItemClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onItemClick("Events") },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Home, contentDescription = "Main Screen")
            Spacer(modifier = Modifier.width(16.dp))
            Text("Main Screen", fontWeight = FontWeight.Bold)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onItemClick("Account Details") },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.AccountCircle, contentDescription = "Account Details")
            Spacer(modifier = Modifier.width(16.dp))
            Text("Account Details", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EventList(events: List<Event>, onEventClick: (Event) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(events) { event ->
            EventItem(event, onEventClick)
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun EventItem(event: Event, onEventClick: (Event) -> Unit) {
    Button(
        onClick = { onEventClick(event) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.name, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Date: ${event.startTime.year}, ${event.startTime.month}, ${event.startTime.dayOfMonth}")
        }
    }
}

@Composable
fun AccountDetails(onEditAccountClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Account Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Username: testuser")
        Text(text = "Email: test@example.com")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onEditAccountClick) {
            Text("Edit Account Details", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Username: testuser")
            Text(text = "Email: test@example.com")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onEditAccountClick) {
                Text("Edit Account Details")
            }
        }
    }
}