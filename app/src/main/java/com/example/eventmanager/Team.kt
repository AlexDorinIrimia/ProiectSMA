package com.example.eventmanager

data class Team(
    val name: String, // Added team name
    val teamLeader: User,
    val members: MutableList<User> = mutableListOf()
) {

    init {
        // Automatically add the team leader as a member upon team creation
        addMember(teamLeader)
    }

    fun addMember(member: User): Boolean {
        if (!members.contains(member)) {
            members.add(member)
            // Ensure the user also knows they are in this team
            if (!member.teams.contains(this)) {
                member.addTeam(this)
            }
            return true
        }
        return false
    }

    fun removeMember(member: User): Boolean {
        // Prevent removing the team leader
        if (member == teamLeader) {
            return false
        }
        if (members.contains(member)) {
            members.remove(member)
            // Ensure the user also knows they are no longer in this team
            if (member.teams.contains(this)) {
                member.removeTeam(this)
            }
            return true
        }
        return false
    }
}