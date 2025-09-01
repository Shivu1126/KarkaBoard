package com.sivaram.karkaboard.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ResetPasswordPref {
    private val Context.dataStore by preferencesDataStore("reset_pref")

    private val RESET_IN_PROGRESS = booleanPreferencesKey("reset_in_progress")

    suspend fun setResetInProgress(context: Context, value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[RESET_IN_PROGRESS] = value
        }
    }

    fun isResetInProgress(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[RESET_IN_PROGRESS] == true
        }
    }
}