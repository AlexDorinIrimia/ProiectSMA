package com.example.eventmanager

import android.content.Context
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.input.key.type
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException
import java.util.UUID

object UserDatabase {
    private const val USER_DATA_FILE = "user_data.json"
    private var users: MutableList<User> = mutableListOf()

    init {
        // Add some predefined users when the UserDatabase is initialized
        if (users.isEmpty()) {
            addPredefinedUsers()
        }
    }

    private fun addPredefinedUsers() {
        val predefinedUsers = listOf(
            User(
                fullName = "John Doe",
                username = "john.doe",
                id = UUID.randomUUID().toString()
            ),
            User(
                fullName = "Jane Smith",
                username = "jane.smith",
                id = UUID.randomUUID().toString()
            ),
            User(
                fullName = "Peter Jones",
                username = "peter.jones",
                id = UUID.randomUUID().toString()
            ),
            User(
                fullName = "Alice Williams",
                username = "alice.williams",
                id = UUID.randomUUID().toString()
            ),
            User(
                fullName = "Bob Brown",
                username = "bob.brown",
                id = UUID.randomUUID().toString()
            ),
            User(
                fullName = "Charlie Davis",
                username = "charlie.davis",
                id = UUID.randomUUID().toString()
            ),
            User(
                fullName = "David Miller",
                username = "david.miller",
                id = UUID.randomUUID().toString()
            ),
            User(
                fullName = "Emily Wilson",
                username = "emily.wilson",
                id = UUID.randomUUID().toString()
            ),
            User(
                fullName = "Frank Moore",
                username = "frank.moore",
                id = UUID.randomUUID().toString()
            ),
            User(
                fullName = "Grace Taylor",
                username = "grace.taylor",
                id = UUID.randomUUID().toString()
            )
        )
        users.addAll(predefinedUsers)
    }

    fun loadUsers(context: Context) {
        val file = File(context.filesDir, USER_DATA_FILE)
        if (file.exists()) {
            try {
                val json = file.readText()
                val type = object : TypeToken<List<User>>() {}.type
                users = Gson().fromJson(json, type)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            // If the file doesn't exist, add predefined users and save them
            addPredefinedUsers()
            saveUsers(context)
        }
    }

    fun saveUsers(context: Context) {
        try {
            val json = Gson().toJson(users)
            val file = File(context.filesDir, USER_DATA_FILE)
            file.writeText(json)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun addUser(user: User, context: Context): Boolean {
        if (users.any { it.username == user.username }) {
            return false // Username already exists
        }
        users.add(user)
        saveUsers(context)
        return true
    }

    fun findUserByUsername(username: String): User? {
        return users.find { it.username == username }
    }

    fun getAllUsers(): List<User> {
        return users
    }

    fun updateUser(user: User, context: Context) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users[index] = user
            saveUsers(context)
        }
    }
}