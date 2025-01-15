package com.example.eventmanager

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.ui.input.key.type
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type

object TeamDatabase {
    private const val TEAMS_FILE_NAME = "teams.json"
    private val teams = mutableListOf<Team>()

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(User::class.java, UserAdapter())
        .create()

    fun addTeam(team: Team, context: Context) {
        teams.add(team)
        saveTeams(context)
    }

    fun removeTeam(team: Team, context: Context) {
        teams.remove(team)
        saveTeams(context)
    }

    fun getAllTeams(): List<Team> {
        return teams
    }

    fun getTeamByName(name: String): Team? {
        return teams.find { it.name == name }
    }

    fun saveTeams(context: Context) {
        val file = File(context.filesDir, TEAMS_FILE_NAME)
        val json = gson.toJson(teams)
        Log.d("TeamDatabase", "Saving teams: $json")
        try {
            FileWriter(file).use { it.write(json) }
        } catch (e: Exception) {
            Log.e("TeamDatabase", "Error saving teams", e)
        }
    }

    fun loadTeams(context: Context) {
        val file = File(context.filesDir, TEAMS_FILE_NAME)
        Log.d("TeamDatabase", "loadTeams() called. File exists: ${file.exists()}")
        if (!file.exists()) {
            return
        }
        try {
            FileReader(file).use { reader ->
                val type: Type = object : TypeToken<List<Team>>() {}.type
                val teamList: List<Team> = gson.fromJson(reader, type)
                teams.clear()
                teams.addAll(teamList)
                Log.d("TeamDatabase", "Loaded ${teams.size} teams")
                teams.forEach { team ->
                    Log.d("TeamDatabase", "Team ${team.name} has ${team.members.size} members")
                }
            }
        } catch (e: Exception) {
            Log.e("TeamDatabase", "Error loading teams", e)
        }
    }
}


class UserAdapter : JsonSerializer<User>, JsonDeserializer<User> {

    override fun serialize(
        src: User?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if (src == null) {
            return com.google.gson.JsonNull.INSTANCE
        }
        val jsonObject = com.google.gson.JsonObject()
        jsonObject.addProperty("fullName", src.fullName)
        jsonObject.addProperty("profilePhoto", src.profilePhoto)
        jsonObject.addProperty("username", src.username)
        jsonObject.addProperty("id", src.id)
        // Do not serialize the teams list or the events list to avoid circular references
        return jsonObject
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): User {
        if (json == null || json.isJsonNull) {
            throw JsonParseException("User JSON is null")
        }
        val jsonObject = json.asJsonObject
        val fullName = jsonObject.get("fullName").asString
        val profilePhoto = if (jsonObject.has("profilePhoto")) jsonObject.get("profilePhoto").asString else null
        val username = jsonObject.get("username").asString
        val id = jsonObject.get("id").asString
        // Do not deserialize the teams list or the events list to avoid circular references
        return User(fullName, profilePhoto, username = username, id = id)
    }
}
