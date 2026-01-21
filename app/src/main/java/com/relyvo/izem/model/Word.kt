package com.relyvo.izem.model

data class Word(
    val id: String = "",
    val categoryId: String = "",
    val english: String = "",
    val arabic: String = "",
    val tamazight: String = "",
    val tifinagh: String = "",
    val imageUrl: String = "",
    val audioUrl: String = "",
    val imageName: String = "",
    val audioName: String = ""
)