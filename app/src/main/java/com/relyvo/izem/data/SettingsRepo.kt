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
        val COMPLETED_LESSONS_KEY = stringPreferencesKey("completed_curriculum_lessons")
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

    val completedLessons: Flow<Map<String, Int>> = context.dataStore.data
        .map { preferences ->
            val raw = preferences[COMPLETED_LESSONS_KEY] ?: ""
            if (raw.isBlank()) emptyMap()
            else {
                raw.split(",")
                    .mapNotNull { entry ->
                        val parts = entry.split(":")
                        if (parts.size == 2) {
                            parts[0] to (parts[1].toIntOrNull() ?: 3)
                        } else null
                    }.toMap()
            }
        }

    suspend fun saveLessonProgress(lessonId: String, stars: Int) {
        context.dataStore.edit { preferences ->
            val raw = preferences[COMPLETED_LESSONS_KEY] ?: ""
            val currentMap = if (raw.isBlank()) mutableMapOf()
            else {
                raw.split(",")
                    .mapNotNull { entry ->
                        val parts = entry.split(":")
                        if (parts.size == 2) {
                            parts[0] to (parts[1].toIntOrNull() ?: 3)
                        } else null
                    }.toMap().toMutableMap()
            }
            val existing = currentMap[lessonId] ?: 0
            if (stars > existing) {
                currentMap[lessonId] = stars
            }
            preferences[COMPLETED_LESSONS_KEY] = currentMap.entries.joinToString(",") { "${it.key}:${it.value}" }
        }
    }
}