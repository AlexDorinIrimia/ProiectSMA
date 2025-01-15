package com.example.eventmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eventmanager.ui.theme.EventManagerTheme
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


sealed class Screen {
    data object Events : Screen()
    data object AccountDetails : Screen()
    data class EventDetails(val event: com.example.eventmanager.Events) : Screen()
    data object EditAccountDetails : Screen() {
        val user: User? = null
    }

    data object AddEvent : Screen()
    data class AddAnnouncement(val event: com.example.eventmanager.Events) : Screen()
    data class AddTeam(val event: com.example.eventmanager.Events) : Screen()
    data class EditTeam(val event: com.example.eventmanager.Events, val team: Team) : Screen()
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MutableCollectionMutableState")
class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserDatabase.loadUsers(this) // Load users from file
        EventsDatabase.loadEvents(this) // Load events from file
        setContent {
            EventManagerTheme {
                // Get the current user data from the intent
                val userFullName = intent.getStringExtra("userFullName") ?: ""
                val userProfilePhoto = intent.getStringExtra("userProfilePhoto")
                val userUsername = intent.getStringExtra("userUsername") ?: ""
                val userId = intent.getStringExtra("userId") ?: ""

                // Create a User object
                val currentUser = User(
                    fullName = userFullName,
                    profilePhoto = userProfilePhoto,
                    username = userUsername,
                    id = userId
                )

                // Load users and events from the databases
                val users = remember { mutableStateOf(UserDatabase.getAllUsers()) }
                val events = remember { mutableStateOf(EventsDatabase.getAllEvents()) }

                MainScreen(
                    users = users.value,
                    events = events.value,
                    onUserUpdated = { updatedUser ->
                        UserDatabase.updateUser(updatedUser, this)
                        users.value = UserDatabase.getAllUsers()
                    },
                    onEventAdded = { newEvent ->
                        EventsDatabase.addEvent(newEvent, this)
                        events.value = EventsDatabase.getAllEvents()
                    },
                    onEventUpdated = { updatedEvent ->
                        // Update the event in the database
                        EventsDatabase.removeEvent(updatedEvent, this)
                        EventsDatabase.addEvent(updatedEvent, this)
                        events.value = EventsDatabase.getAllEvents()
                    },
                    onEventRemoved = { eventToRemove ->
                        EventsDatabase.removeEvent(eventToRemove, this)
                        events.value = EventsDatabase.getAllEvents()
                    },
                    onTeamUpdated = { event, team ->
                        // Update the event in the database
                        EventsDatabase.removeEvent(event, this)
                        EventsDatabase.addEvent(event, this)
                        events.value = EventsDatabase.getAllEvents()
                    },
                    onTeamRemoved = { event, team ->
                        // Update the event in the database
                        EventsDatabase.removeEvent(event, this)
                        EventsDatabase.addEvent(event, this)
                        events.value = EventsDatabase.getAllEvents()
                    },
                    onTeamAdded = { event, team ->
                        // Update the event in the database
                        EventsDatabase.removeEvent(event, this)
                        EventsDatabase.addEvent(event, this)
                        events.value = EventsDatabase.getAllEvents()
                    },
                    currentUser = currentUser,
                    context = this,
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    users: List<User>,
    events: List<Events>,
    onUserUpdated: (User) -> Unit,
    onEventAdded: (Events) -> Unit,
    onEventUpdated: (Events) -> Unit,
    onEventRemoved: (Events) -> Unit,
    onTeamUpdated: (Events, Team) -> Unit,
    onTeamRemoved: (Events, Team) -> Unit,
    onTeamAdded: (Events, Team) -> Unit,
    currentUser: User?,
    context: Context,
) {
    var currentScreen: Screen by remember { mutableStateOf(Screen.Events) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedEvent by remember { mutableStateOf<Events?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(onItemClick = { screen ->
                    currentScreen = when (screen) {
                        "Events" -> Screen.Events
                        "Account Details" -> Screen.AccountDetails
                        else -> Screen.Events
                    }
                    scope.launch { drawerState.close() }
                }, onLogout = {
                    scope.launch { drawerState.close() }
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                })
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
                                is Screen.AddTeam -> "Add Team"
                                is Screen.EditTeam -> "Edit Team"
                            },
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        val innerPadding = PaddingValues(16.dp)
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
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }

                            is Screen.EditAccountDetails -> {
                                IconButton(onClick = {
                                    currentScreen = Screen.AccountDetails
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }

                            is Screen.AddEvent -> {
                                IconButton(onClick = { currentScreen = Screen.Events }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }

                            is Screen.AddAnnouncement -> AddAnnouncementScreen(
                                event = selectedEvent!!,
                                onAnnouncementAdded = { event, announcementData ->
                                    val announcement = Announcement(announcementData as AnnouncementData)
                                    if (context is Activity) {
                                        announcement.sendNotification(context = context)
                                    }
                                    val updatedEvent = event.copy(announcements = event.announcements + announcementData)
                                    onEventUpdated(updatedEvent)
                                    selectedEvent = updatedEvent
                                    currentScreen = Screen.EventDetails(updatedEvent)
                                },
                                onBack = {
                                    currentScreen = Screen.EventDetails(selectedEvent!!)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                            is Screen.AddTeam -> AddTeamScreen(
                                event = selectedEvent!!,
                                onTeamAdded = { event, team ->
                                    onTeamAdded(event, team)
                                    currentScreen = Screen.EventDetails(event)
                                },
                                onBack = {
                                    currentScreen = Screen.EventDetails(selectedEvent!!)
                                },
                                modifier = Modifier.padding(innerPadding),
                                allUsers = users
                            )

                            is Screen.EditTeam -> {
                                IconButton(onClick = {
                                    currentScreen = Screen.EventDetails(selectedEvent!!)
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        }
                    },
                )
            },
            floatingActionButton = {
                if (currentScreen is Screen.Events) {
                    FloatingActionButton(onClick = { currentScreen = Screen.AddEvent }) {
                        Icon(Icons.Filled.Add, "Add")
                    }
                }
            }
        ) { innerPadding ->
            when (currentScreen) {
                is Screen.Events -> EventList(
                    events = events,
                    onEventClick = { event: Events ->
                        selectedEvent = event
                        currentScreen = Screen.EventDetails(event)
                    },
                    modifier = Modifier.padding(innerPadding)
                )

                is Screen.AccountDetails -> AccountDetails(
                    user = currentUser!!,
                    onEditClick = { currentScreen = Screen.EditAccountDetails },
                    modifier = Modifier.padding(innerPadding),
                    currentUser = currentUser,
                    onEditAccountClick = {
                        currentScreen = Screen.EditAccountDetails
                    }
                )

                is Screen.EditAccountDetails -> (currentScreen as Screen.EditAccountDetails).user?.let {
                    EditAccountDetails(
                        user = it,
                        onUserUpdated = onUserUpdated,
                        onBack = { currentScreen = Screen.AccountDetails },
                        modifier = Modifier.padding(innerPadding),
                        currentUser = currentUser,
                        onSave = {
                            onUserUpdated(it)
                            currentScreen = Screen.AccountDetails
                        }
                    )
                }

                is Screen.EventDetails -> EventDetailsScreen(
                    event = (currentScreen as Screen.EventDetails).event,
                    allEvents = events,
                    onAnnouncementAdded = { updatedEvent, announcementData ->
                        val announcement = Announcement(announcementData)
                        if (context is Activity) {
                            announcement.sendNotification(context = context)
                        }
                        onEventUpdated(updatedEvent)
                    },
                    onTeamAdded = { event, team ->
                        onTeamAdded(event, team)
                    },
                    onTeamUpdated = onTeamUpdated,
                    onTeamRemoved = onTeamRemoved,
                    modifier = Modifier.padding(innerPadding),
                    onAddTeamClick = {
                        currentScreen = Screen.AddTeam((currentScreen as Screen.EventDetails).event)
                    },
                    onAddAnnouncementClick = {
                        currentScreen = Screen.AddAnnouncement((currentScreen as Screen.EventDetails).event)
                    }
                )

                is Screen.AddEvent -> AddEventScreen(
                    onEventAdded = onEventAdded,
                    onBack = { currentScreen = Screen.Events },
                    modifier = Modifier.padding(innerPadding)
                )

                is Screen.AddAnnouncement -> AddAnnouncementScreen(
                    event = selectedEvent!!,
                    onAnnouncementAdded = { event, announcementData ->
                        val announcement = Announcement(announcementData as AnnouncementData)
                        if (context is Activity) {
                            announcement.sendNotification(context = context)
                        }
                        val updatedEvent = event.copy(announcements = event.announcements + announcementData)
                        onEventUpdated(updatedEvent)
                        selectedEvent = updatedEvent
                        currentScreen = Screen.EventDetails(updatedEvent)
                    },
                    onBack = {
                        currentScreen = Screen.EventDetails(selectedEvent!!)
                    },
                    modifier = Modifier.padding(innerPadding)
                )

                is Screen.AddTeam -> AddTeamScreen(
                    event = selectedEvent!!,
                    onTeamAdded = { event, team ->
                        onTeamAdded(event, team)
                        currentScreen = Screen.EventDetails(event)
                    },
                    onBack = {
                        currentScreen = Screen.EventDetails(selectedEvent!!)
                    },
                    modifier = Modifier.padding(innerPadding),
                    allUsers = users
                )

                is Screen.EditTeam -> EditTeamScreen(
                    event = (currentScreen as Screen.EditTeam).event,
                    team = (currentScreen as Screen.EditTeam).team,
                    onTeamUpdated = { event: Events, team: Team ->
                        onTeamUpdated(event, team)
                        currentScreen = Screen.EventDetails(event)
                    },
                    onBack = {
                        currentScreen = Screen.EventDetails(selectedEvent!!)
                    },
                    modifier = Modifier.padding(innerPadding),
                    allUsers = users,
                    onTeamEdited = {
                        currentScreen = Screen.EventDetails(selectedEvent!!)
                    },
                    eventUsers = selectedEvent!!.users
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState",
    "NewApi"
)
@Composable
fun EditAccountDetails(
    currentUser: User?,
    onSave: (User) -> Unit,
    onBack: () -> Unit,
    user: Any,
    onUserUpdated: (User) -> Unit,
    modifier: Modifier,
) {
    var username by remember { mutableStateOf(currentUser?.username ?: "") }
    var fullName by remember { mutableStateOf(currentUser?.fullName ?: "") }

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
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val updatedUser = currentUser?.copy(username = username, fullName = fullName)
                if (updatedUser != null) {
                    onSave(updatedUser)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
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
fun DrawerContent(onItemClick: (String) -> Unit, onLogout: Any) {
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
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onItemClick("Events") },
            verticalAlignment = Alignment.CenterVertically){

            Icon(Icons.Filled.Close, contentDescription = "Main Screen")
            Spacer(modifier = Modifier.width(16.dp))
            Text("Log Out", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EventList(events: List<Events>, onEventClick: (Events) -> Unit, modifier: Modifier) {
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
            Text(text = "Date: ${event.startTime.format(DateTimeFormatter.ofPattern("yyyy, MMMM, dd"))}")
        }
    }
}

@Composable
fun AccountDetails(
    currentUser: User?,
    onEditAccountClick: () -> Unit,
    modifier: Modifier,
    onEditClick: () -> Unit,
    user: User,
) {
    var user by remember { mutableStateOf(currentUser) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Account Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        if (user != null) {
            Text(text = "Username: ${user!!.username}")
            Text(text = "Full Name: ${user!!.fullName}")
        } else {
            Text(text = "Loading user data...")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onEditAccountClick) {
            Text("Edit Account Details")
        }
    }
}