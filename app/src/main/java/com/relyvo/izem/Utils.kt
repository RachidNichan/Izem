package com.relyvo.izem

import android.content.Context

object Utils {
    fun getDrawableId(context: Context, name: String): Int {
        if (name.isEmpty()) return 0

        return context.resources.getIdentifier(
            name,
            "drawable",
            context.packageName
        )
    }

    fun getAudioId(context: Context, name: String): Int {
        if (name.isEmpty()) return 0

        return context.resources.getIdentifier(
            name,
            "raw",
            context.packageName
        )
    }
}