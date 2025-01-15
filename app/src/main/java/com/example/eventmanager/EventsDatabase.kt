package com.example.eventmanager

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.size
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.LocalDateTime
@RequiresApi(Build.VERSION_CODES.O)
object EventsDatabase {
    private const val EVENTS_FILE_NAME = "events.json"
    private val events = mutableListOf<Events>()

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Recurrence::class.java, RecurrenceDeserializer())
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    fun addEvent(event: Events, context: Context) {
        events.add(event)
        event.teams.forEach { team ->
            TeamDatabase.addTeam(team, context)
        }
        saveEvents(context)
    }

    fun removeEvent(event: Events, context: Context) {
        events.remove(event)
        saveEvents(context)
    }

    fun getAllEvents(): List<Events> {
        return events
    }

    fun saveEvents(context: Context) {
        val file = File(context.filesDir, EVENTS_FILE_NAME)
        val json = gson.toJson(events)
        Log.d("EventsDatabase", "Saving events: $json")
        try {
            FileWriter(file).use { it.write(json) }
        } catch (e: Exception) {
            Log.e("EventsDatabase", "Error saving events", e)
        }
    }

    fun loadEvents(context: Context) {
        val file = File(context.filesDir, EVENTS_FILE_NAME)
        Log.d("EventsDatabase", "loadEvents() called. File exists: ${file.exists()}")
        if (!file.exists()) {
            return
        }
        try {
            FileReader(file).use { reader ->
                val eventArray = gson.fromJson(reader, Array<Events>::class.java)
                events.clear()
                events.addAll(eventArray)
                Log.d("EventsDatabase", "Loaded ${events.size} events")
                events.forEach { event ->
                    Log.d("EventsDatabase", "Event ${event.name} has ${event.announcements.size} announcements")
                }
            }
        } catch (e: Exception) {
            Log.e("EventsDatabase", "Error loading events", e)
        }
    }
}

// LocalDateTimeAdapter
class LocalDateTimeAdapter : com.google.gson.JsonSerializer<LocalDateTime>,
    com.google.gson.JsonDeserializer<LocalDateTime> {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: java.lang.reflect.Type?,
        context: com.google.gson.JsonSerializationContext?
    ): com.google.gson.JsonElement {
        return com.google.gson.JsonPrimitive(formatter.format(src))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(
        json: com.google.gson.JsonElement?,
        typeOfT: java.lang.reflect.Type?,
        context: com.google.gson.JsonDeserializationContext?
    ): LocalDateTime {
        return LocalDateTime.parse(json?.asString, formatter)
    }
}