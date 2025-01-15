package com.example.eventmanager

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDateTime
import kotlin.random.Random


@SuppressLint("NewApi")
@Composable
fun AddEventScreen(
    onEventAdded: (Events) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedDayOfWeek by remember { mutableStateOf<DayOfWeek?>(null) }
    var selectedDayOfMonth by remember { mutableIntStateOf(1) }
    var isWeeklySelected by remember { mutableStateOf(false) }
    var isMonthlySelected by remember { mutableStateOf(false) }
    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }

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
                DayOfWeek.entries.forEach { day ->
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
                    startTime = selectedDateTime,
                    recurrence = when {
                        isWeeklySelected && selectedDayOfWeek != null -> Recurrence.Weekly(
                            selectedDayOfWeek!!
                        )

                        isMonthlySelected -> Recurrence.Monthly(selectedDayOfMonth)
                        else -> null
                    },
                    id = Random.nextInt().toString(),
                    announcements = mutableListOf<Announcement>(),
                    teams = mutableListOf<Team>(),
                    users = mutableListOf<User>()
                )
                onEventAdded(newEvent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Event")
        }
    }
}
