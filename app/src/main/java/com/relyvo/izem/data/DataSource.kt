package com.relyvo.izem.data

import com.relyvo.izem.R
import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Word

object DataSource {

    val categories = listOf(
        Category("alphabet", "Alphabet", R.drawable.alphabet),
        Category("greetings", "Greetings", R.drawable.greetings),
        Category("family", "Family", R.drawable.family),
        Category("numbers", "Numbers", R.drawable.numbers),
        Category("colors", "Colors", R.drawable.colors),
        Category("animals", "Animals", R.drawable.animals)
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
        Word("num5", "Five", "Semmus", "ⵙⵎⵎⵓⵙ", R.drawable.five, R.raw.semmus),
        Word("num6", "Six", "Sdis", "ⵙⴹⵉⵙ", R.drawable.six, R.raw.sdis),
        Word("num7", "Seven", "Sa", "ⵙⴰ", R.drawable.seven, R.raw.sa),
        Word("num8", "Eight", "Tam", "ⵜⴰⵎ", R.drawable.eight, R.raw.tam),
        Word("num9", "Nine", "Tza", "ⵜⵥⴰ", R.drawable.nine, R.raw.tza),
        Word("num10", "Ten", "Mraw", "ⵎⵔⴰⵡ", R.drawable.ten, R.raw.mraw)
    )

    val colorsList = listOf(
        Word("col1", "White", "Amllal", "ⴰⵎⵍⵍⴰⵍ", R.drawable.white, R.raw.amllal),
        Word("col2", "Black", "Aberkan", "ⴰⴱⵔⴽⴰⵏ", R.drawable.black, R.raw.aberkan),
        Word("col3", "Red", "Azggwagh", "ⴰⵣⴳⴳⵯⴰⵖ", R.drawable.red, R.raw.azggwagh),
        Word("col4", "Green", "Azegza", "ⴰⵣⴳⵣⴰ", R.drawable.green, R.raw.azegza),
        Word("col5", "Blue", "Anili", "ⴰⵏⵉⵍⵉ", R.drawable.blue, R.raw.anili),
        Word("col6", "Yellow", "Awragh", "ⴰⵡⵔⴰⵖ", R.drawable.yellow, R.raw.awragh)
    )

    val animalsList = listOf(
        Word("an1", "Lion", "Izem", "ⵉⵣⵎ", R.drawable.lion, R.raw.izem),
        Word("an2", "Cat", "Amouch", "ⴰⵎⵓⵛ", R.drawable.cat, R.raw.amouch),
        Word("an3", "Dog", "Aydi", "ⴰⵢⴷⵉ", R.drawable.dog, R.raw.aydi),
        Word("an4", "Horse", "Ayyis", "ⴰⵢⵢⵉⵙ", R.drawable.horse, R.raw.ayyis),
        Word("an5", "Camel", "Alghom", "ⴰⵍⵖⵓⵎ", R.drawable.camel, R.raw.alghom),
        Word("an6", "Bird", "Agdid", "ⴰⴳⴹⵉⴹ", R.drawable.bird, R.raw.agdid)
    )

    val alphabetList = listOf(
        // Vowels
        Word("let_a", "A", "A", "ⴰ", null, R.raw.let_a),
        Word("let_e", "E (Schwa)", "E", "ⴻ", null, R.raw.let_e),
        Word("let_i", "I", "I", "ⵉ", null, R.raw.let_i),
        Word("let_u", "U", "U", "ⵓ", null, R.raw.let_u),

        // Consonants
        Word("let_b", "B", "B", "ⴱ", null, R.raw.let_b),
        Word("let_c", "C (Sh)", "C", "ⵛ", null, R.raw.let_c),
        Word("let_d", "D", "D", "ⴷ", null, R.raw.let_d),
        Word("let_d_emph", "Ḍ", "Ḍ", "ⴹ", null, R.raw.let_d_emph),
        Word("let_f", "F", "F", "ⴼ", null, R.raw.let_f),
        Word("let_g", "G", "G", "ⴳ", null, R.raw.let_g),
        Word("let_gw", "Gʷ", "Gʷ", "ⴳⵯ", null, R.raw.let_gw),
        Word("let_h", "H", "H", "ⵀ", null, R.raw.let_h),
        Word("let_h_emph", "Ḥ", "Ḥ", "ⵃ", null, R.raw.let_h_emph),
        Word("let_ayin", "Ɛ", "Ɛ", "ⵄ", null, R.raw.let_ayin),
        Word("let_j", "J", "J", "ⵊ", null, R.raw.let_j),
        Word("let_k", "K", "K", "ⴽ", null, R.raw.let_k),
        Word("let_kw", "Kʷ", "Kʷ", "ⴽⵯ", null, R.raw.let_kw),
        Word("let_l", "L", "L", "ⵍ", null, R.raw.let_l),
        Word("let_m", "M", "M", "ⵎ", null, R.raw.let_m),
        Word("let_n", "N", "N", "ⵏ", null, R.raw.let_n),
        Word("let_q", "Q", "Q", "ⵇ", null, R.raw.let_q),
        Word("let_r", "R", "R", "ⵔ", null, R.raw.let_r),
        Word("let_r_emph", "Ṛ", "Ṛ", "ⵕ", null, R.raw.let_r_emph),
        Word("let_s", "S", "S", "ⵙ", null, R.raw.let_s),
        Word("let_s_emph", "Ṣ", "Ṣ", "ⵚ", null, R.raw.let_s_emph),
        Word("let_t", "T", "T", "ⵜ", null, R.raw.let_t),
        Word("let_t_emph", "Ṭ", "Ṭ", "ⵟ", null, R.raw.let_t_emph),
        Word("let_w", "W", "W", "ⵡ", null, R.raw.let_w),
        Word("let_kh", "X", "X", "ⵅ", null, R.raw.let_kh),
        Word("let_y", "Y", "Y", "ⵢ", null, R.raw.let_y),
        Word("let_z", "Z", "Z", "ⵣ", null, R.raw.let_z),
        Word("let_z_emph", "Ẓ", "Ẓ", "ⵥ", null, R.raw.let_z_emph),
        Word("let_gh", "Ɣ", "Ɣ", "ⵖ", null, R.raw.let_gh)
    )

    fun getWordsByCategory(categoryId: String): List<Word> {
        return when (categoryId) {
            "greetings" -> greetingsList
            "family" -> familyList
            "numbers" -> numbersList
            "colors" -> colorsList
            "animals" -> animalsList
            else -> emptyList()
        }
    }

    val allWords: List<Word>
        get() = greetingsList + familyList + numbersList + colorsList + animalsList
}