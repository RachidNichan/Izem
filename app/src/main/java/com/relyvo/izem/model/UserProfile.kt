package com.relyvo.izem.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class UserProfile(
    val userId: String = "",
    val displayName: String = "Izem",
    val avatarId: Int = 0,
    @get:PropertyName("isPremium")
    var isPremium: Boolean = false,
    val totalXP: Int = 0,
    val currentLevel: String = "Izem Amezwaru",
    val lastActive: Timestamp? = null,
    val learnedWords: List<String> = emptyList(),
    val learningDays: Int = 0,
    val lastDateActive: String = ""
)
