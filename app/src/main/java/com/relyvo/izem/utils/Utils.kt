package com.relyvo.izem.utils

import android.content.Context
import com.relyvo.izem.R

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

    fun getAvatarResource(avatarId: Int): Int {
        return when (avatarId) {
            1 -> R.drawable.avatar_01
            2 -> R.drawable.avatar_02
            3 -> R.drawable.avatar_03
            4 -> R.drawable.avatar_04
            5 -> R.drawable.avatar_05
            6 -> R.drawable.avatar_06
            7 -> R.drawable.avatar_07
            8 -> R.drawable.avatar_08
            9 -> R.drawable.avatar_09
            10 -> R.drawable.avatar_10
            else -> 0
        }
    }
}
