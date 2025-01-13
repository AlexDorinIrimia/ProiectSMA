package com.example.eventmanager

class Team(val teamLeader: String) {

    companion object {
        const val MAX_MEMBERS = 4
    }

    private val members: MutableList<String> = mutableListOf()

    fun addMember(memberName: String): Boolean {
        if (members.size < MAX_MEMBERS) {
            members.add(memberName)
            return true
        }
        return false
    }


    fun removeMember(memberName: String): Boolean {
        return members.remove(memberName)
    }

    fun getMembers(): List<String> {
        return members.toList() // Return a copy to prevent external modification
    }

    fun getMemberCount(): Int {
        return members.size
    }

    fun isFull(): Boolean {
        return members.size == MAX_MEMBERS
    }

    fun isEmpty(): Boolean {
        return members.isEmpty()
    }

    fun getTeamLeader(): String {
        return teamLeader
    }

    fun clearMembers() {
        members.clear()
    }

    fun containsMember(memberName: String): Boolean {
        return members.contains(memberName)
    }
}