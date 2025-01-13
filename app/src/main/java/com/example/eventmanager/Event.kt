package com.example.eventmanager

import android.annotation.SuppressLint
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

class Events(
    val name: String,
    val place: String,
    val startTime: LocalDateTime,
    val recurrence: Recurrence? = null // Added recurrence property
) {
    private val teams: MutableList<Team> = mutableListOf()
    private val teamDeadlines: MutableMap<String, LocalDateTime> = mutableMapOf()

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
            teamDeadlines.remove(team.getTeamLeader())
            return true
        }
        return false
    }

    fun getTeams(): List<Team> {
        return teams.toList()
    }

    @SuppressLint("NewApi")
    fun setTeamDeadline(teamLeader: String, deadline: LocalDateTime): Boolean {
        if (teams.any { it.getTeamLeader() == teamLeader }) {
            teamDeadlines[teamLeader] = deadline
            return true
        }
        return false
    }

    fun getTeamDeadline(teamLeader: String): LocalDateTime? {
        return teamDeadlines[teamLeader]
    }

    fun getAllTeamDeadlines(): Map<String, LocalDateTime> {
        return teamDeadlines.toMap()
    }

    fun getName(): String {
        return name
    }

    fun getPlace(): String {
        return place
    }

    fun getStartTime(): LocalDateTime {
        return startTime
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
}

sealed class Recurrence {

    data class Weekly(val dayOfWeek: DayOfWeek) : Recurrence()

    data class Monthly(val dayOfMonth: Int) : Recurrence()
}