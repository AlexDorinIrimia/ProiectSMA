package com.example.eventmanager

import android.annotation.SuppressLint
import android.content.Context
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters


class Events(
    val name: String,
    val place: String,
    val startTime: LocalDateTime,
    val recurrence: Recurrence? = null,
    val teams: MutableList<Team> = mutableListOf(),
    val users: MutableList<User> = mutableListOf(),
    val announcements: MutableList<Announcement> = mutableListOf(),
) {

    private val teamDeadlines: MutableMap<String, LocalDateTime> = mutableMapOf()

    fun addTeam(team: Team): Boolean {
        if (!teams.contains(team)) {
            teams.add(team)
            // Ensure all team members are also added as users to the event
            team.members.forEach { addUser(it) }
            return true
        }
        return false
    }

    fun removeTeam(team: Team): Boolean {
        if (teams.contains(team)) {
            teams.remove(team)
            // Remove team deadlines
            teamDeadlines.remove(team.teamLeader.fullName)
            return true
        }
        return false
    }

    fun addUser(user: User): Boolean {
        if (!users.contains(user)) {
            users.add(user)
            // Ensure the user also knows they are in this event
            if (!user.events.contains(this)) {
                user.addEvent(this)
            }
            return true
        }
        return false
    }

    fun removeUser(user: User): Boolean {
        if (users.contains(user)) {
            users.remove(user)
            // Ensure the user also knows they are no longer in this event
            if (user.events.contains(this)) {
                user.removeEvent(this)
            }
            return true
        }
        return false
    }

    fun addAnnouncement(announcement: Announcement, context: Context): Boolean {
        if (!announcements.contains(announcement)) {
            announcements.add(announcement)
            // Send the notification
            announcement.sendNotification(context)
            return true
        }
        return false
    }

    fun removeAnnouncement(announcement: Announcement): Boolean {
        if (announcements.contains(announcement)) {
            announcements.remove(announcement)
            return true
        }
        return false
    }

    @SuppressLint("NewApi")
    fun setTeamDeadline(teamLeader: User, deadline: LocalDateTime): Boolean {
        if (teams.any { it.teamLeader == teamLeader }) {
            teamDeadlines[teamLeader.fullName] = deadline
            return true
        }
        return false
    }

    fun getTeamDeadline(teamLeader: User): LocalDateTime? {
        return teamDeadlines[teamLeader.fullName]
    }

    fun getAllTeamDeadlines(): Map<String, LocalDateTime> {
        return teamDeadlines.toMap()
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