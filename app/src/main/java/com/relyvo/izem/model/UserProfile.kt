package com.relyvo.izem.model

data class UserProfile(
    val totalXP: Int = 0,
    val currentLevel: String = "Izem Amezwaru",
    val lastActive: com.google.firebase.Timestamp? = null,
    val learnedWords: List<String> = emptyList(),
    val learningDays: Int = 0,
    val lastDateActive: String = ""
)