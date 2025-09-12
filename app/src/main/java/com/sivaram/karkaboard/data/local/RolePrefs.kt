package com.sivaram.karkaboard.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object RolePrefs {
    private val Context.dataStore by preferencesDataStore("user_prefs")

    val USER_ROLE = stringPreferencesKey("user_role")

    suspend fun saveRole(context: Context, role: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ROLE] = role
        }
    }

    fun getRole(context: Context): Flow<String> {

        return context.dataStore.data.map { prefs ->
            prefs[USER_ROLE] ?: "Unknown"
        }
    }

    suspend fun clear(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
