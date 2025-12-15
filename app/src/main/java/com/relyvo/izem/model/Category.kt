package com.relyvo.izem.model

import androidx.annotation.DrawableRes

data class Category(
    val id: String,
    val title: String,
    @DrawableRes val iconRes: Int
)