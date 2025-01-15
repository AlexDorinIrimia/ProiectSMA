package com.example.eventmanager

import java.io.Serializable

data class User(
    val fullName: String,
    val profilePhoto: String? = null,
    val events: MutableList<Events> = mutableListOf(),
    val teams: MutableList<Team> = mutableListOf(),
    val username: String,
    val id: String
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
    fun isInEvent(event: Events): Boolean {
        return event.users.contains(this)
    }
}