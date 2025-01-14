package com.example.eventmanager

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eventmanager.ui.theme.EventManagerTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
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
    data class EventDetails(val event: com.example.eventmanager.Events) : Screen()
    data object EditAccountDetails : Screen()
    data object AddEvent : Screen()
    data class AddAnnouncement(val event: com.example.eventmanager.Events) : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi")
@Composable
fun MainScreen() {
    var currentScreen: Screen by remember { mutableStateOf(Screen.Events) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val user1 = User("Alice Smith", "alice@example.com", Uri.parse("content://..."))
    val user2 = User("Bob Johnson", "bob@example.com")
    val user3 = User("Charlie Brown", "charlie@example.com")

    // Sample Teams
    val teamA = Team("Team A",user1) // user1 is the team leader
    teamA.addMember(user2)
    val teamB = Team("Team B",user3)

    // Sample Announcements
    val announcement1 = Announcement("Welcome to the Tech Conference!")
    val announcement2 = Announcement("Don't forget the Summer Music Festival!")

    // Sample Current User
    val currentUser = user1
    val events by remember {
        mutableStateOf(
            mutableListOf(
                Events(
                    name = "Tech Conference 2024",
                    place = "Convention Center",
                    startTime = LocalDateTime.of(2024, 10, 26, 9, 0),
                    teams = mutableListOf(teamA),
                    announcements = mutableListOf(announcement1)
                ),
                Events(
                    name = "Summer Music Festival",
                    place = "Central Park",
                    startTime = LocalDateTime.of(2024, 7, 15, 12, 0),
                    teams = mutableListOf(teamB),
                    announcements = mutableListOf(announcement2)
                ),
                Events(
                    name = "Weekly Meeting",
                    place = "Office",
                    startTime = LocalDateTime.of(2024, 1, 15, 9, 0),
                    recurrence = Recurrence.Weekly(DayOfWeek.MONDAY),
                    teams = mutableListOf(teamA, teamB)
                ),
                Events(
                    name = "Monthly Review",
                    place = "Boardroom",
                    startTime = LocalDateTime.of(2024, 1, 1, 10, 0),
                    recurrence = Recurrence.Monthly(1),
                    teams = mutableListOf(teamA)
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
                                is Screen.AddAnnouncement -> "Add Announcement"
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

                            is Screen.AddAnnouncement -> {
                                val addAnnouncementScreen = currentScreen as Screen.AddAnnouncement
                                IconButton(onClick = { currentScreen = Screen.EventDetails(addAnnouncementScreen.event) }) {
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

                    is Screen.EventDetails -> {
                        val eventDetailsScreen = currentScreen as Screen.EventDetails
                        EventDetailsScreen(
                            event = eventDetailsScreen.event,
                            modifier = Modifier.padding(innerPadding),
                            onAddAnnouncementClick = { event ->
                                currentScreen = Screen.AddAnnouncement(event)
                            }
                        )
                    }
                    is Screen.AddAnnouncement -> {
                        val addAnnouncementScreen = currentScreen as Screen.AddAnnouncement
                        AddAnnouncementScreen(
                            event = addAnnouncementScreen.event,
                            onAnnouncementAdded = {
                                // Handle the new announcement if needed
                                // For example, you might want to refresh the event details screen
                                currentScreen = Screen.EventDetails(addAnnouncementScreen.event)
                            },
                            onBack = {
                                currentScreen = Screen.EventDetails(addAnnouncementScreen.event)
                            }
                        )
                    }

                    is Screen.AccountDetails -> AccountDetails(
                        onEditAccountClick = { currentScreen = Screen.EditAccountDetails }
                    )

                    is Screen.AddEvent -> AddEventScreen(onEventAdded = { newEvent: Events ->
                        events.add(newEvent)
                        currentScreen = Screen.Events
                    })

                    is Screen.EditAccountDetails -> EditAccountDetails(
                        onSave = { currentScreen = Screen.AccountDetails })
                }
            }
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun AddEventScreen(onEventAdded: (Events) -> Unit, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedDayOfWeek by remember { mutableStateOf<DayOfWeek?>(null) }
    var selectedDayOfMonth by remember { mutableIntStateOf(1) }
    var isWeeklySelected by remember { mutableStateOf(false) }
    var isMonthlySelected by remember { mutableStateOf(false) }

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
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Recurrence:")

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isWeeklySelected,
                onCheckedChange = { isChecked ->
                    isWeeklySelected = isChecked
                    if (isChecked) {
                        isMonthlySelected = false
                    }
                }
            )
            Text("Weekly")
        }

        if (isWeeklySelected) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DayOfWeek.values().forEach { day ->
                    Button(onClick = { selectedDayOfWeek = day }) {
                        Text(day.name.take(3))
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isMonthlySelected,
                onCheckedChange = { isChecked ->
                    isMonthlySelected = isChecked
                    if (isChecked) {
                        isWeeklySelected = false
                    }
                }
            )
            Text("Monthly")
        }
        if (isMonthlySelected) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (1..31).forEach { day ->
                    Button(onClick = { selectedDayOfMonth = day }) {
                        Text(day.toString())
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val newEvent = Events(
                    name = name,
                    place = location,
                    startTime = LocalDateTime.now(),
                    recurrence = when {
                        isWeeklySelected && selectedDayOfWeek != null -> Recurrence.Weekly(selectedDayOfWeek!!)
                        isMonthlySelected -> Recurrence.Monthly(selectedDayOfMonth)
                        else -> null
                    },
                    teams = mutableListOf(),
                    users = mutableListOf(),
                    announcements = mutableListOf()
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
fun AddAnnouncementScreen(event: Events, onAnnouncementAdded: () -> Unit, onBack: () -> Unit) {
    var announcementText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Add Announcement for ${event.name}",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = announcementText,
            onValueChange = { announcementText = it },
            label = { Text("Announcement") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (announcementText.isNotBlank()) {
                    val newAnnouncement = Announcement(announcementText)
                    event.announcements.add(newAnnouncement)
                    onAnnouncementAdded()
                    announcementText = "" // Clear the text field
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Announcement")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
@Composable
fun EventDetailsScreen(
    event: Events,
    modifier: Modifier = Modifier,
    onAddAnnouncementClick: (Events) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(text = "Event Name: ${event.name}")
        Text(text = "Place: ${event.place}")
        Text(text = "Start Time: ${event.startTime}")
        Text(text = "Recurrence: ${event.recurrence}")
        Text(text = "Teams:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            event.teams.forEach { team ->
                Text(text = "Team: ${team.name}", fontWeight = FontWeight.Bold) // Display team name
                Text(text = "Team Leader: ${team.teamLeader.fullName}")
                team.members.forEach { member ->
                    if (member != team.teamLeader) {
                        Text(text = "- ${member.fullName}")
                    }
                }
            }
        }


        // Add Announcement Button (only for team leaders)
        val currentUser = User("testuser", "test@example.com") // Replace with your current user
        if (currentUser.isTeamLeaderForEvent(event)) {
            Button(onClick = { onAddAnnouncementClick(event) }) {
                Text("Add Announcement")
            }
        }

        // Display Announcements
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Announcements:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(event.announcements) { announcement ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = announcement.announcement)
                    }
                }
            }
        }
        Button(onClick = { onAddAnnouncementClick(event) }) {
            Icon(Icons.Filled.Add, contentDescription = "Add Announcement")
            Text("Add Announcement")
        }
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
fun EventList(events: List<Events>, onEventClick: (Events) -> Unit) {
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
fun EventItem(event: Events, onEventClick: (Events) -> Unit) {
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