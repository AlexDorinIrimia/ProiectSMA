package com.example.eventmanager

import android.net.Uri


data class User(
    val fullName: String,
    val email: String,
    val profilePhoto: Uri? = null,
    val teams: MutableList<Team> = mutableListOf(),
    val events: MutableList<Events> = mutableListOf()
) {
    fun isTeamLeaderForEvent(event: Events): Boolean {
        return event.teams.any { it.teamLeader == this }
    }
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

    fun addEvent(event: Events): Boolean {
        if (!events.contains(event)) {
            events.add(event)
            return true
        }
        return false
    }

    fun removeEvent(event: Events): Boolean {
        if (events.contains(event)) {
            events.remove(event)
            return true
        }
        return false
    }
}