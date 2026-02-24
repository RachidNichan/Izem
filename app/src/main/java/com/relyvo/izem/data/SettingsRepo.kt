package com.relyvo.izem.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "izem_settings")

class SettingsRepo(private val context: Context) {

    companion object {
        val IS_ARABIC_KEY = booleanPreferencesKey("is_arabic")
        val PREFERRED_VARIETY_KEY = stringPreferencesKey("preferred_variety")
    }

    val isArabic: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_ARABIC_KEY] ?: false
        }

    suspend fun setArabic(isArabic: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_ARABIC_KEY] = isArabic
        }
    }

    val preferredVariety: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PREFERRED_VARIETY_KEY] ?: "Standard"
        }

    suspend fun setVariety(variety: String) {
        context.dataStore.edit { preferences ->
            preferences[PREFERRED_VARIETY_KEY] = variety
        }
    }
}