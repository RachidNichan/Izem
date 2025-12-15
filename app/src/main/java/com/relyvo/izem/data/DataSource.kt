package com.relyvo.izem.data

import com.relyvo.izem.R
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Word

object DataSource {

    val categories = listOf(
        Category("greetings", "Greetings", R.drawable.greetings),
        Category("family", "Family", R.drawable.family),
        Category("numbers", "Numbers", R.drawable.numbers)
    )

    val greetingsList = listOf(
        Word("1", "Hello", "Azul", "ⴰⵣⵓⵍ", R.drawable.hello_icon, R.raw.azul),
        Word("2", "Thank you", "Tanmmirt", "ⵜⴰⵏⵎⵎⵉⵔⵜ", R.drawable.thank_you, R.raw.tanmmirt),
        Word("3", "Water", "Aman", "ⴰⵎⴰⵏ", R.drawable.water, R.raw.aman),
        Word("4", "Bread", "Aghrom", "ⴰⵖⵔⵓⵎ", R.drawable.bread, R.raw.aghrom)
    )

    val familyList = listOf(
        Word("5", "Man", "Argaz", "ⴰⵔⴳⴰⵣ", R.drawable.man, R.raw.argaz),
        Word("6", "Woman", "Tamghart", "ⵜⴰⵎⵖⴰⵔⵜ", R.drawable.woman, R.raw.tamghart),
    )

    val numbersList: List<Word> = listOf(
        Word("num1", "One", "Yan", "ⵢⴰⵏ", R.drawable.one, R.raw.yan),
        Word("num2", "Two", "Sin", "ⵙⵉⵏ", R.drawable.two, R.raw.sin),
        Word("num3", "Three", "Krad", "ⴽⵕⴰⴹ", R.drawable.three, R.raw.krad),
        Word("num4", "Four", "Koz", "ⴽⵓⵥ", R.drawable.four, R.raw.koz),
        Word("num5", "Five", "Semmus", "ⵙⵎⵎⵓⵙ", R.drawable.five, R.raw.semmus)
    )

    fun getWordsByCategory(categoryId: String): List<Word> {
        return when (categoryId) {
            "greetings" -> greetingsList
            "family" -> familyList
            "numbers" -> numbersList
            else -> emptyList()
        }
    }

    val allWords: List<Word>
        get() = greetingsList + familyList + numbersList
}