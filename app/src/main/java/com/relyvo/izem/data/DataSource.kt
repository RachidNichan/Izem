package com.relyvo.izem.data

import com.relyvo.izem.R
import com.relyvo.izem.model.Word

object DataSource {
    val words = listOf(
        Word(
            id = "1",
            english = "Hello",
            tamazight = "Azul",
            tifinagh = "ⴰⵣⵓⵍ",
            audioRes = R.raw.azul
        ),
        Word(
            id = "2",
            english = "Thank you",
            tamazight = "Tanmmirt",
            tifinagh = "ⵜⴰⵏⵎⵎⵉⵔⵜ",
            audioRes = R.raw.tanmmirt
        ),
        Word(
            id = "3",
            english = "Water",
            tamazight = "Aman",
            tifinagh = "ⴰⵎⴰⵏ",
            audioRes = null
        ),
        Word(
            id = "4",
            english = "Bread",
            tamazight = "Aghrom",
            tifinagh = "ⴰⵖⵔⵓⵎ",
            audioRes = null
        ),
        Word(
            id = "5",
            english = "Man",
            tamazight = "Argaz",
            tifinagh = "ⴰⵔⴳⴰⵣ",
            audioRes = null
        ),
        Word(
            id = "6",
            english = "Woman",
            tamazight = "Tamghart",
            tifinagh = "ⵜⴰⵎⵖⴰⵔⵜ",
            audioRes = null
        )
    )
}