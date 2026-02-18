package com.relyvo.izem.model

import com.google.firebase.Timestamp

data class Suggestion(
    val id: String = "",
    val type: String = "NEW",
    val categoryId: String = "",
    val dialect: String = "Standard", // (IRCAM, Souss, Atlas, Rif)
    val english: String = "",
    val arabic: String = "",
    val tamazight: String = "",
    val tifinagh: String = "",
    val audioUrl: String = "",
    val imageUrl: String = "",
    val status: String = "pending",   // "pending", "approved", "rejected"
    val userId: String = "",
    val createdAt: Timestamp? = null
)