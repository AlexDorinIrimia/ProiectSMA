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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.eventmanager.ui.theme.EventManagerTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime

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

sealed class Screen {
    data object Events : Screen()
    data object AccountDetails : Screen()
    data class EventDetails(val event: Event) : Screen()
    data object EditAccountDetails : Screen()
    data object AddEvent : Screen()
}

data class Event(
    val id: Int,
    val name: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String? = null,
    val organizer: String? = null,
    val maxAttendees: Int? = null,
    val currentAttendees: Int = 0,
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi")
@Composable
fun MainScreen() {
    var currentScreen: Screen by remember { mutableStateOf(Screen.Events) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var events by remember {
        mutableStateOf(
            listOf(
                Event(
                    id = 1,
                    name = "Tech Conference 2024",
                    description = "A conference for tech enthusiasts.",
                    startTime = LocalDateTime.of(2024, 10, 26, 9, 0),
                    endTime = LocalDateTime.of(2024, 10, 28, 17, 0),
                    location = "Convention Center",
                    maxAttendees = 500,
                    currentAttendees = 250
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
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    onItemClick = { screen ->
                        currentScreen = when(screen)
                        {
                            "Events" -> Screen.Events
                            "Account Details" -> Screen.AccountDetails
                            else -> Screen.Events
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentScreen) {
                                is Screen.Events -> "Events"
                                is Screen.AccountDetails -> "Account Details"
                                is Screen.EventDetails -> "Event Details"
                                is Screen.EditAccountDetails -> "Edit Account Details"
                                is Screen.AddEvent -> "Add Event"
                            },
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        when (currentScreen) {
                            is Screen.Events, is Screen.AccountDetails -> {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Menu",
                                        tint = Color.White
                                    )
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
                            is Screen.AddEvent -> {
                                IconButton(onClick = { currentScreen = Screen.Events }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        }
                    },
                    actions = {
                        if (currentScreen is Screen.Events) {
                            FloatingActionButton(
                                onClick = {
                                    currentScreen = Screen.AddEvent
                                },
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                Icon(Icons.Filled.Add, "Add")
                            }
                        }
                    }
                )
            },
            content = { innerPadding ->
                when (currentScreen) {
                    is Screen.Events -> EventList(
                        events = events,
                        onEventClick = { event ->
                            currentScreen = Screen.EventDetails(event)
                        }
                    )

                    is Screen.AccountDetails -> AccountDetails(
                        onEditAccountClick = { currentScreen = Screen.EditAccountDetails }
                    )

                    is Screen.EventDetails -> EventDetailsScreen(
                        event = (currentScreen as Screen.EventDetails).event
                    )

                    is Screen.EditAccountDetails -> EditAccountDetails()
                    is Screen.AddEvent -> AddEventScreen(
                        onEventAdded = { newEvent ->
                            events = events + newEvent
                            currentScreen = Screen.Events
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun AddEventScreen(onEventAdded: (Event) -> Unit, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var organizer by remember { mutableStateOf("") }
    var maxAttendees by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Add New Event",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Event Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = organizer,
            onValueChange = { organizer = it },
            label = { Text("Organizer") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = maxAttendees,
            onValueChange = { maxAttendees = it },
            label = { Text("Max Attendees") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val newEvent = Event(
                    id = (System.currentTimeMillis() % 10000).toInt(), // Simple ID generation
                    name = name,
                    description = description,
                    startTime = LocalDateTime.now(), // You might want to add a date/time picker
                    endTime = LocalDateTime.now().plusHours(2), // Default end time 2 hours later
                    location = location.ifEmpty { null },
                    organizer = organizer.ifEmpty { null },
                    maxAttendees = maxAttendees.toIntOrNull(),
                    currentAttendees = 0
                )
                onEventAdded(newEvent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Event")
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
fun EditAccountDetails(onSave: () -> Unit = {}) {
    var username by remember { mutableStateOf("testuser") }
    var email by remember { mutableStateOf("test@example.com") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Edit Account Details",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Here you would save the updated account details
                // For now, we'll just call the onSave callback
                onSave()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
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
        contentPadding = PaddingValues(64.dp)
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

        }
    }
}