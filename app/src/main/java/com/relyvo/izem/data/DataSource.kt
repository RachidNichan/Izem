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
            audioRes = R.raw.azul,
            imageRes = R.drawable.hello_icon
        ),
        Word(
            id = "2",
            english = "Thank you",
            tamazight = "Tanmmirt",
            tifinagh = "ⵜⴰⵏⵎⵎⵉⵔⵜ",
            audioRes = R.raw.tanmmirt,
            imageRes = null
        ),
        Word(
            id = "3",
            english = "Water",
            tamazight = "Aman",
            tifinagh = "ⴰⵎⴰⵏ",
            audioRes = null,
            imageRes = R.drawable.water
        ),
        Word(
            id = "4",
            english = "Bread",
            tamazight = "Aghrom",
            tifinagh = "ⴰⵖⵔⵓⵎ",
            audioRes = null,
            imageRes = R.drawable.bread
        ),
        Word(
            id = "5",
            english = "Man",
            tamazight = "Argaz",
            tifinagh = "ⴰⵔⴳⴰⵣ",
            audioRes = null,
            imageRes = null
        ),
        Word(
            id = "6",
            english = "Woman",
            tamazight = "Tamghart",
            tifinagh = "ⵜⴰⵎⵖⴰⵔⵜ",
            audioRes = null,
            imageRes = null
        )
    )
}