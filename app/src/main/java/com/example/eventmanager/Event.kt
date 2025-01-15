package com.example.eventmanager

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

class Events(
    val id: String,
    val name: String,
    val place: String,
    val startTime: LocalDateTime,
    val recurrence: Recurrence? = null,
    val announcements: MutableList<Announcement> = ArrayList(),
    val teams: MutableList<Team> = mutableListOf(),
    val users: MutableList<User> = mutableListOf(),
    val joinRequests: MutableList<User> = mutableListOf()
) {
    fun addTeam(team: Team): Boolean {
        if (!teams.contains(team)) {
            teams.add(team)
            return true
        }
        return false
    }

    fun removeTeam(team: Team): Boolean {
        if (teams.contains(team)) {
            teams.remove(team)
            return true
        }
        return false
    }

    @SuppressLint("NewApi")
    fun getNextOccurrence(after: LocalDateTime): LocalDateTime? {
        return when (recurrence) {
            is Recurrence.Weekly -> {
                val nextOccurrence = startTime.with(TemporalAdjusters.next(recurrence.dayOfWeek))
                if (nextOccurrence.isAfter(after)) {
                    nextOccurrence
                } else {
                    nextOccurrence.plusWeeks(1)
                }
            }
            is Recurrence.Monthly -> {
                val nextOccurrence = startTime.withDayOfMonth(recurrence.dayOfMonth)
                if (nextOccurrence.isAfter(after)) {
                    nextOccurrence
                } else {
                    nextOccurrence.plusMonths(1)
                }
            }
            else -> null
        }
    }

    fun copy(announcements: List<Any?>): Events {
    return Events(
        id = id,
        name = name,
        place = place,
        startTime = startTime,
        recurrence = recurrence,
    )
    }

}

sealed class Recurrence {
    data class Weekly(val dayOfWeek: DayOfWeek) : Recurrence()
    data class Monthly(val dayOfMonth: Int) : Recurrence()
}

// Custom JsonDeserializer for Recurrence
class RecurrenceDeserializer : JsonDeserializer<Recurrence> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Recurrence {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString

        return when (type) {
            "weekly" -> {
                val dayOfWeek = DayOfWeek.valueOf(jsonObject.get("dayOfWeek").asString)
                Recurrence.Weekly(dayOfWeek)
            }
            "monthly" -> {
                val dayOfMonth = jsonObject.get("dayOfMonth").asInt
                Recurrence.Monthly(dayOfMonth)
            }
            else -> throw JsonParseException("Unknown recurrence type: $type")
        }
    }
}