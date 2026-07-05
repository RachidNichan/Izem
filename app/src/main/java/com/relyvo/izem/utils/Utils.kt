package com.relyvo.izem.utils

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

    fun findActivity(context: Context): android.app.Activity? {
        var currentContext = context
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is android.app.Activity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }
}
