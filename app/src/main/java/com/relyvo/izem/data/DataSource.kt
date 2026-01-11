package com.relyvo.izem.data

import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Word

object DataSource {

    val categories = listOf(
        Category("alphabet", "Alphabet", "الحروف", "alphabet"),
        Category("greetings", "Greetings", "التحية", "greetings"),
        Category("family", "Family", "العائلة", "family"),
        Category("numbers", "Numbers", "الأرقام", "numbers"),
        Category("colors", "Colors", "الألوان", "colors"),
        Category("animals", "Animals", "الحيوانات", "animals")
    )

    val greetingsList = listOf(
        Word("1", "greetings", "Hello", "مرحباً", "Azul", "ⴰⵣⵓⵍ", "hello_icon", "azul"),
        Word("2", "greetings", "Thank you", "شكراً", "Tanmmirt", "ⵜⴰⵏⵎⵎⵉⵔⵜ", "thank_you", "tanmmirt"),
        Word("3", "greetings", "Water", "ماء", "Aman", "ⴰⵎⴰⵏ", "water", "aman"),
        Word("4", "greetings", "Bread", "خبز", "Aghrom", "ⴰⵖⵔⵓⵎ", "bread", "aghrom")
    )

    val familyList = listOf(
        Word("fam1", "family", "Man", "رجل", "Argaz", "ⴰⵔⴳⴰⵣ", "man", "argaz"),
        Word("fam2", "family", "Woman", "امرأة", "Tamghart", "ⵜⴰⵎⵖⴰⵔⵜ", "woman", "tamghart"),
        Word("fam3", "family", "Father", "أب", "Baba", "ⴱⴰⴱⴰ", "father", "baba"),
        Word("fam4", "family", "Mother", "أم", "Yemma", "ⵢⵎⵎⴰ", "mother", "yemma"),
        Word("fam5", "family", "Brother", "أخ", "Gma", "ⴳⵎⴰ", "brother", "gma"),
        Word("fam6", "family", "Sister", "أخت", "Ultma", "ⵓⵍⵜⵎⴰ", "sister", "ultma"),
        Word("fam7", "family", "Boy / Son", "ولد / ابن", "Afrukh", "ⴰⴼⵔⵓⵅ", "boy", "afrukh"),
        Word("fam8", "family", "Girl / Daughter", "بنت / ابنة", "Tafrukht", "ⵜⴰⴼⵔⵓⵅⵜ", "girl", "tafrukht")
    )

    val numbersList = listOf(
        Word("num01", "numbers", "One", "واحد", "Yan", "ⵢⴰⵏ", "one", "yan"),
        Word("num02", "numbers", "Two", "اثنان", "Sin", "ⵙⵉⵏ", "two", "sin"),
        Word("num03", "numbers", "Three", "ثلاثة", "Krad", "ⴽⵕⴰⴹ", "three", "krad"),
        Word("num04", "numbers", "Four", "أربعة", "Koz", "ⴽⵓⵥ", "four", "koz"),
        Word("num05", "numbers", "Five", "خمسة", "Semmus", "ⵙⵎⵎⵓⵙ", "five", "semmus"),
        Word("num06", "numbers", "Six", "ستة", "Sdis", "ⵙⴹⵉⵙ", "six", "sdis"),
        Word("num07", "numbers", "Seven", "سبعة", "Sa", "ⵙⴰ", "seven", "sa"),
        Word("num08", "numbers", "Eight", "ثمانية", "Tam", "ⵜⴰⵎ", "eight", "tam"),
        Word("num09", "numbers", "Nine", "تسعة", "Tza", "ⵜⵥⴰ", "nine", "tza"),
        Word("num10", "numbers", "Ten", "عشرة", "Mraw", "ⵎⵔⴰⵡ", "ten", "mraw")
    )

    val colorsList = listOf(
        Word("col1", "colors", "White", "أبيض", "Amllal", "ⴰⵎⵍⵍⴰⵍ", "white", "amllal"),
        Word("col2", "colors", "Black", "أسود", "Aberkan", "ⴰⴱⵔⴽⴰⵏ", "black", "aberkan"),
        Word("col3", "colors", "Red", "أحمر", "Azggwagh", "ⴰⵣⴳⴳⵯⴰⵖ", "red", "azggwagh"),
        Word("col4", "colors", "Green", "أخضر", "Azegza", "ⴰⵣⴳⵣⴰ", "green", "azegza"),
        Word("col5", "colors", "Blue", "أزرق", "Anili", "ⴰⵏⵉⵍⵉ", "blue", "anili"),
        Word("col6", "colors", "Yellow", "أصفر", "Awragh", "ⴰⵡⵔⴰⵖ", "yellow", "awragh")
    )

    val animalsList = listOf(
        Word("an1", "animals", "Lion", "أسد", "Izem", "ⵉⵣⵎ", "lion", "izem"),
        Word("an2", "animals", "Cat", "قط", "Amouch", "ⴰⵎⵓⵛ", "cat", "amouch"),
        Word("an3", "animals", "Dog", "كلب", "Aydi", "ⴰⵢⴷⵉ", "dog", "aydi"),
        Word("an4", "animals", "Horse", "حصان", "Ayyis", "ⴰⵢⵢⵉⵙ", "horse", "ayyis"),
        Word("an5", "animals", "Camel", "جمل", "Alghom", "ⴰⵍⵖⵓⵎ", "camel", "alghom"),
        Word("an6", "animals", "Bird", "طائر", "Agdid", "ⴰⴳⴹⵉⴹ", "bird", "agdid")
    )

    val alphabetList = listOf(
        // Vowels
        Word("let_a", "alphabet", "A", "أ", "A", "ⴰ", "", "let_a"),
        Word("let_e", "alphabet", "E", "ي (سكون)", "E", "ⴻ", "", "let_e"),
        Word("let_i", "alphabet", "I", "ي", "I", "ⵉ", "", "let_i"),
        Word("let_u", "alphabet", "U", "و", "U", "ⵓ", "", "let_u"),

        // Consonants
        Word("let_b", "alphabet", "B", "ب", "B", "ⴱ", "", "let_b"),
        Word("let_c", "alphabet", "C", "ش", "C", "ⵛ", "", "let_c"),
        Word("let_d", "alphabet", "D", "د", "D", "ⴷ", "", "let_d"),
        Word("let_d_emph", "alphabet", "Ḍ", "ض", "Ḍ", "ⴹ", "", "let_d_emph"),
        Word("let_f", "alphabet", "F", "ف", "F", "ⴼ", "", "let_f"),
        Word("let_g", "alphabet", "G", "گ", "G", "ⴳ", "", "let_g"),
        Word("let_gw", "alphabet", "Gʷ", "گ (مشمومة)", "Gʷ", "ⴳⵯ", "", "let_gw"),
        Word("let_h", "alphabet", "H", "هـ", "H", "ⵀ", "", "let_h"),
        Word("let_h_emph", "alphabet", "Ḥ", "ح", "Ḥ", "ⵃ", "", "let_h_emph"),
        Word("let_ayin", "alphabet", "Ɛ", "ع", "Ɛ", "ⵄ", "", "let_ayin"),
        Word("let_j", "alphabet", "J", "ج", "J", "ⵊ", "", "let_j"),
        Word("let_k", "alphabet", "K", "ك", "K", "ⴽ", "", "let_k"),
        Word("let_kw", "alphabet", "Kʷ", "ك (مشمومة)", "Kʷ", "ⴽⵯ", "", "let_kw"),
        Word("let_l", "alphabet", "L", "ل", "L", "ⵍ", "", "let_l"),
        Word("let_m", "alphabet", "M", "م", "M", "ⵎ", "", "let_m"),
        Word("let_n", "alphabet", "N", "ن", "N", "ⵏ", "", "let_n"),
        Word("let_q", "alphabet", "Q", "ق", "Q", "ⵇ", "", "let_q"),
        Word("let_r", "alphabet", "R", "ر", "R", "ⵔ", "", "let_r"),
        Word("let_r_emph", "alphabet", "Ṛ", "ر (مفخمة)", "Ṛ", "ⵕ", "", "let_r_emph"),
        Word("let_s", "alphabet", "S", "س", "S", "ⵙ", "", "let_s"),
        Word("let_s_emph", "alphabet", "Ṣ", "ص", "Ṣ", "ⵚ", "", "let_s_emph"),
        Word("let_t", "alphabet", "T", "ت", "T", "ⵜ", "", "let_t"),
        Word("let_t_emph", "alphabet", "Ṭ", "ط", "Ṭ", "ⵟ", "", "let_t_emph"),
        Word("let_w", "alphabet", "W", "و", "W", "ⵡ", "", "let_w"),
        Word("let_kh", "alphabet", "X", "خ", "X", "ⵅ", "", "let_kh"),
        Word("let_y", "alphabet", "Y", "ي", "Y", "ⵢ", "", "let_y"),
        Word("let_z", "alphabet", "Z", "ز", "Z", "ⵣ", "", "let_z"),
        Word("let_z_emph", "alphabet", "Ẓ", "ز (مفخمة)", "Ẓ", "ⵥ", "", "let_z_emph"),
        Word("let_gh", "alphabet", "Ɣ", "غ", "Ɣ", "ⵖ", "", "let_gh")
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