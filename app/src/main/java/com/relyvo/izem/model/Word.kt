package com.relyvo.izem.model

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

data class Word(
    val id: String,
    val english: String,
    val tamazight: String,
    val tifinagh: String,
    @DrawableRes val imageRes: Int? = null,
    @RawRes val audioRes: Int? = null
)