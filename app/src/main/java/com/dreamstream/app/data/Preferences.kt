package com.dreamstream.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("dreamstream_prefs")

class Preferences(private val context: Context) {
    companion object {
        private val SERVER = stringPreferencesKey("server")
        private val USERNAME = stringPreferencesKey("username")
        private val PASSWORD = stringPreferencesKey("password")
    }

    val server: Flow<String> = context.dataStore.data.map { it[SERVER] ?: "http://vpn-esxtrapro.info:80" }
    val username: Flow<String> = context.dataStore.data.map { it[USERNAME] ?: "" }
    val password: Flow<String> = context.dataStore.data.map { it[PASSWORD] ?: "" }

    suspend fun saveCredentials(server: String, username: String, password: String) {
        context.dataStore.edit {
            it[SERVER] = server.trim()
            it[USERNAME] = username.trim()
            it[PASSWORD] = password
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
