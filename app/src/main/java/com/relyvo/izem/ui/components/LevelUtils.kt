package com.relyvo.izem.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.relyvo.izem.R

@Composable
fun getLocalizedLevel(level: String): String {
    return when (level) {
        "level_1", "Izem Amezwaru" -> stringResource(R.string.level_azemwaru)
        "level_2", "Izem Anlmad" -> stringResource(R.string.level_anlmad)
        "level_3", "Izem Amqran" -> stringResource(R.string.level_amqran)
        "level_4", "Agellid n Izmawn" -> stringResource(R.string.level_agellid)
        "Izem" -> stringResource(R.string.level_izem)
        else -> level
    }
}
