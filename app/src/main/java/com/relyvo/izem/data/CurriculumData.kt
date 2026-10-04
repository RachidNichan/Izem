package com.relyvo.izem.data

import com.relyvo.izem.model.Category
import com.relyvo.izem.model.Phrase
import com.relyvo.izem.model.Word
import com.relyvo.izem.model.curriculum.*

object CurriculumData {

    data class CategoryMetadata(
        val order: Int,
        val themeColorHex: Long,
        val defaultTitleEn: String,
        val defaultTitleAr: String,
        val defaultDescEn: String,
        val defaultDescAr: String,
        val guidebookSummaryEn: String,
        val guidebookSummaryAr: String,
        val tipEn: String,
        val tipAr: String
    )

    private val categoryMetadataMap = mapOf(
        "alphabet" to CategoryMetadata(
            order = 1,
            themeColorHex = 0xFF1565C0, // Indigo Blue
            defaultTitleEn = "Unit 1: Tifinagh Alphabet",
            defaultTitleAr = "الوحدة 1: حروف التيفيناغ",
            defaultDescEn = "Master foundational Tifinagh letters, pronunciation, and writing.",
            defaultDescAr = "تعلم الحروف الأبجدية للتيفيناغ، أصواتها وكتابتها الصحيحة.",
            guidebookSummaryEn = "Tifinagh is the ancient script of Tamazight, written from left to right. Each letter represents a distinct Amazigh sound.",
            guidebookSummaryAr = "التيفيناغ هو الخط التاريخي للغة الأمازيغية، يُكتب من اليسار إلى اليمين ويتميز برموزه الهندسية الجميلة.",
            tipEn = "Pay close attention to geometric letter shapes like circle (O - Ya) and cross (+ - Yat).",
            tipAr = "لاحظ الأشكال الهندسية للحروف مثل الدائرة (O - يا) والتقاطع (+ - يات)."
        ),
        "greetings" to CategoryMetadata(
            order = 2,
            themeColorHex = 0xFF2E7D32, // Forest Green
            defaultTitleEn = "Unit 2: Greetings & Politeness",
            defaultTitleAr = "الوحدة 2: التحيات والمجاملات",
            defaultDescEn = "Everyday social greetings, welcoming, and gratitude.",
            defaultDescAr = "التحيات اليومية، الترحيب، عبارات الشكر والامتنان.",
            guidebookSummaryEn = "Greetings in Tamazight are heartfelt. 'Azul' derives from peace and closeness of the heart ('Az' + 'Ul').",
            guidebookSummaryAr = "التحيات في الثقافة الأمازيغية تعبر عن المودة والسلام. «أزول» تعني القرب من القلب والسلام الداخلي.",
            tipEn = "To greet a group, say 'Azul fell-awen'. To thank someone, say 'Tanemmirt'.",
            tipAr = "لتحية جماعة نقول «أزول فلوان»، ولتقديم الشكر نقول «تانميرت»."
        ),
        "pronouns" to CategoryMetadata(
            order = 3,
            themeColorHex = 0xFF00838F, // Teal
            defaultTitleEn = "Unit 3: Pronouns",
            defaultTitleAr = "الوحدة 3: الضمائر المنفصلة",
            defaultDescEn = "Learn subject pronouns: I, you, he, she, we, they.",
            defaultDescAr = "الضمائر الشخصية المنفصلة: أنا، أنتَ، أنتِ، هو، هي...",
            guidebookSummaryEn = "Tamazight distinguishes gender in the second and third persons (you masculine vs. you feminine).",
            guidebookSummaryAr = "تميز الأمازيغية بين المذكر والمؤنث في ضمائر المخاطب والغائب بدقة عالية.",
            tipEn = "Nekk = I, Kečč = You (masc), Kemm = You (fem), Netta = He, Nettat = She.",
            tipAr = "نك = أنا، كتش = أنتَ، كم = أنتِ، نتا = هو، نتات = هي."
        ),
        "numbers" to CategoryMetadata(
            order = 4,
            themeColorHex = 0xFFEF6C00, // Amber Orange
            defaultTitleEn = "Unit 4: Numbers & Counting",
            defaultTitleAr = "الوحدة 4: الأرقام والعد",
            defaultDescEn = "Count numbers in Tamazight and master numerical terms.",
            defaultDescAr = "تعلم الأرقام والحساب ومصطلحات العد بالأمازيغية.",
            guidebookSummaryEn = "Amazigh numbers have masculine and feminine forms, used with nouns across daily commerce.",
            guidebookSummaryAr = "الأرقام في الأمازيغية تمتلك صيغاً للمذكر والمؤنث وتستخدم في الحساب والمعاملات اليومية.",
            tipEn = "Yiwen (1), Sin (2), Kraḍ (3), Kuẓ (4), Semmus (5), Sḍis (6), Sa (7), Tam (8), Tẓa (9), Mraw (10).",
            tipAr = "ييون (1)، سين (2)، كراض (3)، كوز (4)، سموس (5)، سديس (6)، سا (7)، تام (8)، تزا (9)، مراو (10)."
        ),
        "family" to CategoryMetadata(
            order = 5,
            themeColorHex = 0xFFC2185B, // Rose Crimson
            defaultTitleEn = "Unit 5: Family & Kinship",
            defaultTitleAr = "الوحدة 5: العائلة والقرابة",
            defaultDescEn = "Vocabulary for parents, siblings, relatives, and family life.",
            defaultDescAr = "مفردات الوالدين، الإخوة، الأقارب والروابط العائلية.",
            guidebookSummaryEn = "Family bonds (Tawacult) hold a central sacred place in Amazigh social structure.",
            guidebookSummaryAr = "تحظى الأسرة (تاواشولت) بمكانة مركزية ومقدسة في النسيج الاجتماعي الأمازيغي.",
            tipEn = "Baba = Father, Yemma = Mother, Gma / Uma = Brother, Ultma = Sister.",
            tipAr = "بابا = الأب، يما = الأم، أوما = الأخ، أولتما = الأخت."
        ),
        "colors" to CategoryMetadata(
            order = 6,
            themeColorHex = 0xFF7B1FA2, // Purple
            defaultTitleEn = "Unit 6: Colors",
            defaultTitleAr = "الوحدة 6: الألوان",
            defaultDescEn = "Describe objects and nature with vibrant Tamazight colors.",
            defaultDescAr = "وصف الأشياء والطبيعة بالألوان الأمازيغية الزاهية.",
            guidebookSummaryEn = "Color adjectives in Tamazight often begin with 'A-' in masculine and 'Ta-' in feminine form.",
            guidebookSummaryAr = "صفات الألوان في الأمازيغية تبدأ غالباً بالسابقة «أ-» للمذكر و«تا-» للمؤنث.",
            tipEn = "Amellal (White), Aberkan (Black), Azegzaw (Blue/Green), Azeggaɣ (Red), Awragh (Yellow).",
            tipAr = "أملال (أبيض)، أبركان (أسود)، أزكزا (أزرق/أخضر)، أزكاغ (أحمر)، أوراغ (أصفر)."
        ),
        "food" to CategoryMetadata(
            order = 7,
            themeColorHex = 0xFFD84315, // Rust Orange
            defaultTitleEn = "Unit 7: Food & Nourishment",
            defaultTitleAr = "الوحدة 7: الطعام والغذاء",
            defaultDescEn = "Common foods, staples, fruits, and table expressions.",
            defaultDescAr = "المأكولات والمشروبات اليومية، الفواكه ومفردات المائدة.",
            guidebookSummaryEn = "Amazigh cuisine reflects rich agricultural roots, celebrating couscous, olive oil, and bread.",
            guidebookSummaryAr = "المطبخ الأمازيغي غني بأطباقه التقليدية الأصيلة مثل الكسكس وزيت الزيتون والخبز المنزلي.",
            tipEn = "Aɣrum = Bread, Aman = Water, Udi = Butter, Tamment = Honey, Zit = Olive Oil.",
            tipAr = "أغروم = الخبز، أمان = الماء، أودي = السمن/الزبدة، تامنت = العسل، زيت = زيت الزيتون."
        ),
        "animals" to CategoryMetadata(
            order = 8,
            themeColorHex = 0xFF4E342E, // Earth Brown
            defaultTitleEn = "Unit 8: Animals",
            defaultTitleAr = "الوحدة 8: الحيوانات",
            defaultDescEn = "Domestic, farm, and wild animals of North Africa.",
            defaultDescAr = "الحيوانات الأليفة، الداجنة والبرية في شمال إفريقيا.",
            guidebookSummaryEn = "Animals carry deep symbolic meaning in Amazigh folk tales, notably the lion (Izem) representing courage.",
            guidebookSummaryAr = "تحمل الحيوانات دلالات رمزية عميقة في التراث الأمازيغي، وخاصة الأسد «إيزم» رمز الشجاعة والأنفة.",
            tipEn = "Izem = Lion, Aydi = Dog, Amchich = Cat, Ayis = Horse, Tafunast = Cow.",
            tipAr = "إيزم = الأسد، أيدي = الكلب، أمشيش = القط، أييس = الحصان، تافوناست = البقرة."
        ),
        "nature" to CategoryMetadata(
            order = 9,
            themeColorHex = 0xFF00695C, // Emerald Teal
            defaultTitleEn = "Unit 9: Nature & Landscapes",
            defaultTitleAr = "الوحدة 9: الطبيعة والبيئة",
            defaultDescEn = "Mountains, valleys, sky, sun, rain, and earth.",
            defaultDescAr = "الجبال، الوديان، السماء، الشمس، المطر والأرض.",
            guidebookSummaryEn = "The Amazigh relationship with nature is deeply reverent; landscapes shape the language's rich geography.",
            guidebookSummaryAr = "علاقة الأمازيغ بالأرض والطبيعة وثيقة جداً، وتزخر اللغة بمسميات دقيقة للتضاريس والمناخ.",
            tipEn = "Adrar = Mountain, Asif = River, Tafukt = Sun, Aggur = Moon, Igenni = Sky.",
            tipAr = "أدرار = الجبل، أسيف = النهر/الوادي، تافوكت = الشمس، أَيُّور = القمر، إكني = السماء."
        ),
        "clothes" to CategoryMetadata(
            order = 10,
            themeColorHex = 0xFF37474F, // Slate Grey
            defaultTitleEn = "Unit 10: Clothes & Attire",
            defaultTitleAr = "الوحدة 10: الملابس واللباس",
            defaultDescEn = "Garments, traditional wear, jewelry, and shoes.",
            defaultDescAr = "الملابس اليومية والتقليدية، الحلي الأمازيغية والأحذية.",
            guidebookSummaryEn = "Traditional Amazigh garments feature geometric embroidery and symbolic silver fibulae (Tizerzai).",
            guidebookSummaryAr = "تتميز الأزياء الأمازيغية التقليدية بنقوشها الهندسية المتقنة وحلي الفضة العريقة (تيزرزاي).",
            tipEn = "Aqchab / Tajellabt = Cloak/Djellaba, Isemdal = Shoes, Tisila = Sandals.",
            tipAr = "أقشاب / تاجلابت = الجلباب، إسمدال = الأحذية، تيسيلا = الصنادل."
        ),
        "week" to CategoryMetadata(
            order = 11,
            themeColorHex = 0xFF0277BD, // Ocean Blue
            defaultTitleEn = "Unit 11: Days of the Week",
            defaultTitleAr = "الوحدة 11: أيام الأسبوع",
            defaultDescEn = "The seven days of the week in standard Tamazight.",
            defaultDescAr = "الأيام السبعة للأسبوع باللغة الأمازيغية المعيارية.",
            guidebookSummaryEn = "The days of the week in Tamazight are based on Amazigh root terminology for daily cycles.",
            guidebookSummaryAr = "تعتمد تسميات أيام الأسبوع في الأمازيغية على جذور لغوية أصيلة تصف دورة الأسبوع.",
            tipEn = "Aynas (Mon), Asinas (Tue), Akras (Wed), Akwas (Thu), Simwas (Fri), Saḍyas (Sat), Asamas (Sun).",
            tipAr = "أيناس (الاثنين)، أسيناس (الثلاثاء)، أكراس (الأربعاء)، أكواس (الخميس)، سيمواس (الجمعة)، سادياس (السبت)، أساماس (الأحد)."
        ),
        "months" to CategoryMetadata(
            order = 12,
            themeColorHex = 0xFF4527A0, // Deep Violet
            defaultTitleEn = "Unit 12: Months & Seasons",
            defaultTitleAr = "الوحدة 12: الشهور والفصول",
            defaultDescEn = "The Amazigh agricultural calendar, Yennayer, and seasons.",
            defaultDescAr = "التقويم الفلاحي الأمازيغي، يناير والشهور والفصول.",
            guidebookSummaryEn = "The Amazigh calendar is agricultural, commencing in mid-January with Yennayer (Amazigh New Year).",
            guidebookSummaryAr = "التقويم الأمازيغي تقويم فلاحي مرتبط بالأرض ودورة الزراعة، ويبدأ منتصف يناير بالاحتفال بـ «إيض يناير».",
            tipEn = "Yennayer (January), Furar (February), Meɣres (March), Ibrir (April), Mayyu (May)...",
            tipAr = "يناير، فورار (فبراير)، مغرس (مارس)، إبرير (أبريل)، مايو..."
        )
    )

    fun buildCourseFromFirestore(
        categories: List<Category>,
        allWords: List<Word>,
        phrases: List<Phrase>,
        isArabic: Boolean
    ): Course {
        if (allWords.isEmpty()) {
            return getStarterCourse()
        }

        val preferredCategoryOrder = listOf(
            "alphabet",
            "greetings",
            "pronouns",
            "numbers",
            "family",
            "colors",
            "food",
            "animals",
            "nature",
            "clothes",
            "week",
            "months"
        )

        val presentCategoryIds = (categories.map { it.id } + allWords.map { it.categoryId })
            .filter { it.isNotBlank() }
            .distinct()

        val sortedCategoryIds = preferredCategoryOrder.filter { presentCategoryIds.contains(it) } +
                presentCategoryIds.filter { !preferredCategoryOrder.contains(it) }

        val units = sortedCategoryIds.mapIndexedNotNull { unitIndex, catId ->
            val meta = categoryMetadataMap[catId]
            val catObj = categories.find { it.id.equals(catId, ignoreCase = true) }
            val wordsInCat = allWords.filter { it.categoryId.equals(catId, ignoreCase = true) }

            if (wordsInCat.isEmpty()) {
                null
            } else {
                buildUnitForCategory(
                    order = unitIndex + 1,
                    categoryId = catId,
                    category = catObj,
                    words = wordsInCat,
                    allWords = allWords,
                    phrases = phrases,
                    isArabic = isArabic,
                    meta = meta
                )
            }
        }

        if (units.isEmpty()) {
            return getStarterCourse()
        }

        return Course(
            id = "tamazight_master_course",
            titleEn = "Standard Tamazight Journey",
            titleAr = "مسار تعلم الأمازيغية الشامل",
            descriptionEn = "Learn all 12 categories from alphabet and greetings to vocabulary and grammar.",
            descriptionAr = "تعلم جميع الأقسام من الحروف والتحيات إلى المفردات وقواعد اللغة خطوة بخطوة.",
            units = units
        )
    }

    private fun buildUnitForCategory(
        order: Int,
        categoryId: String,
        category: Category?,
        words: List<Word>,
        allWords: List<Word>,
        phrases: List<Phrase>,
        isArabic: Boolean,
        meta: CategoryMetadata?
    ): LearningUnit {
        val unitId = "unit_$categoryId"
        val themeColor = meta?.themeColorHex ?: 0xFF2E7D32
        val titleEn = category?.titleEn?.takeIf { it.isNotBlank() }?.let { "Unit $order: $it" }
            ?: meta?.defaultTitleEn ?: "Unit $order: ${categoryId.replaceFirstChar { it.uppercase() }}"
        val titleAr = category?.titleAr?.takeIf { it.isNotBlank() }?.let { "الوحدة $order: $it" }
            ?: meta?.defaultTitleAr ?: "الوحدة $order: $categoryId"

        val descEn = meta?.defaultDescEn ?: "Master vocabulary and usage for ${category?.titleEn ?: categoryId}."
        val descAr = meta?.defaultDescAr ?: "تعلم وأتقن مفردات واستعمالات ${category?.titleAr ?: categoryId}."

        val chunkSize = 4
        val chunks = words.chunked(chunkSize)

        val lessons = chunks.mapIndexed { chunkIdx, chunk ->
            buildLessonForChunk(
                lessonOrder = chunkIdx + 1,
                unitId = unitId,
                categoryId = categoryId,
                chunk = chunk,
                wordsInCat = words,
                allWords = allWords,
                phrases = phrases,
                isArabic = isArabic,
                meta = meta
            )
        }

        val guidebook = UnitGuidebook(
            titleEn = "Unit $order Guidebook: ${category?.titleEn ?: categoryId}",
            titleAr = "دليل الوحدة $order: ${category?.titleAr ?: categoryId}",
            summaryEn = meta?.guidebookSummaryEn ?: "Study and review vocabulary and pronunciation for ${category?.titleEn ?: categoryId}.",
            summaryAr = meta?.guidebookSummaryAr ?: "مراجعة ودراسة المفردات والنطق الصحيح لقسم ${category?.titleAr ?: categoryId}.",
            sections = listOf(
                GuidebookSection(
                    headingEn = "Vocabulary Reference (${words.size} words)",
                    headingAr = "جدول الكلمات والمفردات (${words.size} كلمة)",
                    bodyEn = "All terms introduced in this unit:",
                    bodyAr = "جميع المصطلحات التي تتضمنها هذه الوحدة:",
                    examples = words.take(12).map { w ->
                        GuidebookExample(
                            tamazight = w.tamazight,
                            tifinagh = w.tifinagh,
                            translationEn = w.english,
                            translationAr = w.arabic
                        )
                    }
                ),
                GuidebookSection(
                    headingEn = "Grammar & Cultural Note",
                    headingAr = "إضاءة لغوية وثقافية",
                    bodyEn = meta?.tipEn ?: "Practice speaking each word aloud with the correct Amazigh phonetic tone.",
                    bodyAr = meta?.tipAr ?: "احرص على نطق الكلمات بصوت مسموع لترسيخ مخارج الحروف الأمازيغية الصحيحة.",
                    examples = emptyList()
                )
            )
        )

        return LearningUnit(
            id = unitId,
            order = order,
            titleEn = titleEn,
            titleAr = titleAr,
            descriptionEn = descEn,
            descriptionAr = descAr,
            themeColorHex = themeColor,
            iconName = categoryId,
            guidebook = guidebook,
            lessons = lessons
        )
    }

    private fun buildLessonForChunk(
        lessonOrder: Int,
        unitId: String,
        categoryId: String,
        chunk: List<Word>,
        wordsInCat: List<Word>,
        allWords: List<Word>,
        phrases: List<Phrase>,
        isArabic: Boolean,
        meta: CategoryMetadata?
    ): Lesson {
        val lessonId = "${unitId}_lesson_$lessonOrder"
        val titleEn = if (categoryId == "alphabet") {
            "Letters ${chunk.map { it.tifinagh }.joinToString(" ")}"
        } else {
            "Lesson $lessonOrder: " + chunk.take(2).joinToString(", ") { it.tamazight }
        }
        val titleAr = if (categoryId == "alphabet") {
            "الحروف (${chunk.map { it.tifinagh }.joinToString(" ")})"
        } else {
            "الدرس $lessonOrder: " + chunk.take(2).joinToString("، ") { if (it.arabic.isNotBlank()) it.arabic else it.tamazight }
        }

        val descEn = "Master ${chunk.size} items: " + chunk.joinToString(", ") { it.tamazight }
        val descAr = "تعلم ${chunk.size} عناصر: " + chunk.joinToString("، ") { if (it.arabic.isNotBlank()) it.arabic else it.tamazight }

        val steps = mutableListOf<ExerciseStep>()

        // 1. Introduce each word
        chunk.forEach { word ->
            steps.add(
                ExerciseStep.IntroduceCard(
                    id = "intro_${word.id.ifEmpty { word.tamazight }}",
                    word = word,
                    academicTipEn = if (categoryId == "alphabet") "Tifinagh letter '${word.tifinagh}' sounds like '${word.tamazight}'." else meta?.tipEn ?: "Pronounce '${word.tamazight}' (${word.tifinagh}).",
                    academicTipAr = if (categoryId == "alphabet") "حرف التيفيناغ '${word.tifinagh}' يُنطق '${word.tamazight}'." else meta?.tipAr ?: "تدرّب على نطق '${word.tamazight}' (${word.tifinagh})."
                )
            )
        }

        // 2. Multiple choice for each word
        chunk.forEach { word ->
            val distractors = (wordsInCat.filter { it.id != word.id } + allWords.filter { it.id != word.id && it.categoryId != "alphabet" })
                .distinctBy { it.tamazight }
                .shuffled()
                .take(3)

            val correctAns = if (isArabic) word.arabic.ifEmpty { word.tamazight } else word.english.ifEmpty { word.tamazight }
            val wrongOptions = distractors.map { if (isArabic) it.arabic.ifEmpty { it.tamazight } else it.english.ifEmpty { it.tamazight } }
            val options = (listOf(correctAns) + wrongOptions).filter { it.isNotBlank() }.distinct().shuffled()

            steps.add(
                ExerciseStep.MultipleChoice(
                    id = "mc_${word.id.ifEmpty { word.tamazight }}",
                    prompt = if (isArabic) "ما معنى كلمة «${word.tamazight}» (${word.tifinagh})؟" else "What is the meaning of \"${word.tamazight}\" (${word.tifinagh})?",
                    promptSubtext = if (isArabic) "اختر الترجمة الصحيحة" else "Select the correct translation",
                    promptAudioUrl = word.audioUrl,
                    promptImageUrl = word.imageUrl,
                    options = if (options.size >= 2) options else listOf(correctAns, "---"),
                    correctAnswer = correctAns
                )
            )
        }

        // 3. Spelling tile assembly for words with Tifinagh
        if (categoryId != "alphabet") {
            chunk.filter { it.tifinagh.trim().length in 2..7 }.take(2).forEach { word ->
                val clean = word.tifinagh.trim()
                val glyphs = clean.filter { !it.isWhitespace() }.map { it.toString() }
                if (glyphs.size >= 2) {
                    val extraPool = listOf("ⴰ", "ⴱ", "ⴳ", "ⴷ", "ⴻ", "ⴼ", "ⴽ", "ⵍ", "ⵎ", "ⵏ", "ⵔ", "ⵙ", "ⵜ", "ⵢ", "ⵣ")
                        .filter { !glyphs.contains(it) }
                        .shuffled()
                        .take(2)
                    steps.add(
                        ExerciseStep.Spelling(
                            id = "spell_${word.id.ifEmpty { word.tamazight }}",
                            word = word,
                            targetScript = ScriptType.TIFINAGH,
                            scrambledLetters = (glyphs + extraPool).shuffled(),
                            correctSpelling = clean
                        )
                    )
                }
            }
        }

        // 4. Pair matching if chunk has >= 3 items
        if (chunk.size >= 3) {
            val pairs = chunk.take(4).map { w ->
                MatchPairItem(
                    id = w.id.ifEmpty { w.tamazight },
                    amazigh = "${w.tamazight} (${w.tifinagh})",
                    tifinagh = w.tifinagh,
                    translation = if (isArabic) w.arabic.ifEmpty { w.english } else w.english.ifEmpty { w.arabic }
                )
            }
            steps.add(
                ExerciseStep.MatchPairs(
                    id = "match_${lessonId}",
                    pairs = pairs
                )
            )
        }

        // 5. Sentence assembly if category has matching phrases
        val catPhrases = phrases.filter { it.categoryId.equals(categoryId, ignoreCase = true) }
        if (catPhrases.isNotEmpty()) {
            val phrase = catPhrases.shuffled().first()
            val tokens = phrase.tamazight.split(" ").filter { it.isNotBlank() }
            if (tokens.size in 2..6) {
                steps.add(
                    ExerciseStep.AssembleSentence(
                        id = "sentence_${phrase.id.ifEmpty { phrase.tamazight }}",
                        promptTranslationEn = phrase.english,
                        promptTranslationAr = phrase.arabic,
                        correctSentenceTamazight = phrase.tamazight,
                        correctSentenceTifinagh = phrase.tifinagh,
                        scrambledTokens = tokens.shuffled(),
                        correctTokenOrder = tokens,
                        audioUrl = phrase.audioUrl
                    )
                )
            }
        }

        return Lesson(
            id = lessonId,
            unitId = unitId,
            order = lessonOrder,
            titleEn = titleEn,
            titleAr = titleAr,
            descriptionEn = descEn,
            descriptionAr = descAr,
            xpReward = 15,
            steps = steps
        )
    }

    fun getStarterCourse(): Course {
        return Course(
            id = "tamazight_a1",
            titleEn = "Standard Tamazight (A1)",
            titleAr = "أساسيات الأمازيغية (A1)",
            descriptionEn = "From Tifinagh script to everyday conversations, step by step.",
            descriptionAr = "من كتابة حروف التيفيناغ إلى الحوارات اليومية، خطوة بخطوة.",
            units = listOf(
                createUnit1(),
                createUnit2(),
                createUnit3()
            )
        )
    }

    // ==========================================
    // UNIT 1: Tifinagh & First Greetings
    // ==========================================
    private fun createUnit1(): LearningUnit {
        return LearningUnit(
            id = "unit_1",
            order = 1,
            titleEn = "Unit 1: Tifinagh & Greetings",
            titleAr = "الوحدة 1: التيفيناغ والتحيات",
            descriptionEn = "Learn essential Tifinagh letters and everyday greetings.",
            descriptionAr = "تعلم حروف التيفيناغ الأساسية والتحيات اليومية المستخدمة.",
            themeColorHex = 0xFF2E7D32, // Forest Green
            iconName = "waving_hand",
            guidebook = UnitGuidebook(
                titleEn = "Unit 1 Guidebook: Greetings & Script",
                titleAr = "دليل الوحدة 1: التحيات والحروف",
                summaryEn = "In Tamazight, greetings are warm and rooted in history. The script used across North Africa is called Tifinagh (ⵜⵉⴼⵉⵏⴰⵖ).",
                summaryAr = "التحيات في الأمازيغية عريقة وتعبر عن السلام والترحاب. الخط التاريخي المعتمد يسمى التيفيناغ (ⵜⵉⴼⵉⵏⴰⵖ).",
                sections = listOf(
                    GuidebookSection(
                        headingEn = "1. Saying Hello: Azul (ⴰⵣⵓⵍ)",
                        headingAr = "1. إلقاء التحية: أزول (ⴰⵣⵓⵍ)",
                        bodyEn = "Azul is the universal Amazigh greeting. Etymologically, it originates from 'Az' (approach/come close) and 'Ul' (heart), signifying 'peace from the heart'.",
                        bodyAr = "كلمة «أزول» هي التحية الأمازيغية الجامعة، ومعناها الأصيل مقترن بالقرب من القلب والسلام والمودة.",
                        examples = listOf(
                            GuidebookExample("Azul!", "ⴰⵣⵓⵍ!", "Hello / Greetings!", "مرحباً / سلام!"),
                            GuidebookExample("Azul fell-awen!", "ⴰⵣⵓⵍ ⴼⴻⵍⵍ-ⴰⵡⴻⵏ!", "Greetings to you all!", "السلام عليكم جميعاً!")
                        )
                    ),
                    GuidebookSection(
                        headingEn = "2. Expressing Gratitude: Tanemmirt (ⵜⴰⵏⴻⵎⵎⵉⵔⵜ)",
                        headingAr = "2. الشكر والامتنان: تانميرت (ⵜⴰⵏⴻⵎⵎⵉⵔⵜ)",
                        bodyEn = "To thank someone, say 'Tanemmirt'. To reply 'You're welcome', you can say 'Ur yilli uɣilif' (No problem) or 'Ulac aɣilif'.",
                        bodyAr = "لتقديم الشكر نقول «تانميرت»، وللرد «عفواً / لا شكر على واجب» نقول «أولاش أغيليف» أو «أور يلي أوغيليف».",
                        examples = listOf(
                            GuidebookExample("Tanemmirt!", "ⵜⴰⵏⴻⵎⵎⵉⵔⵜ!", "Thank you!", "شكراً جزيلاً!"),
                            GuidebookExample("Tanemmirt bahra!", "ⵜⴰⵏⴻⵎⵎⵉⵔⵜ ⴱⴰⵀⵔⴰ!", "Thank you very much!", "شكراً جزيلاً لك!")
                        )
                    )
                )
            ),
            lessons = listOf(
                createUnit1Lesson1(),
                createUnit1Lesson2(),
                createUnit1Lesson3()
            )
        )
    }

    private fun createUnit1Lesson1(): Lesson {
        val wordAzul = Word(
            id = "w_azul",
            categoryId = "greetings",
            english = "Hello / Greetings",
            arabic = "مرحباً / سلام",
            tamazight = "Azul",
            tifinagh = "ⴰⵣⵓⵍ"
        )
        val wordTanemmirt = Word(
            id = "w_tanemmirt",
            categoryId = "greetings",
            english = "Thank you",
            arabic = "شكراً",
            tamazight = "Tanemmirt",
            tifinagh = "ⵜⴰⵏⴻⵎⵎⵉⵔⵜ"
        )

        return Lesson(
            id = "unit_1_lesson_1",
            unitId = "unit_1",
            order = 1,
            titleEn = "Hello & Welcome",
            titleAr = "التحية والترحيب",
            descriptionEn = "Master 'Azul' and 'Tanemmirt' in Latin and Tifinagh.",
            descriptionAr = "تعلم نطق وكتابة أزول وتانميرت.",
            xpReward = 15,
            steps = listOf(
                ExerciseStep.IntroduceCard(
                    id = "u1_l1_s1",
                    word = wordAzul,
                    academicTipEn = "'Azul' is formed of: ⴰ (A), ⵣ (Z), ⵓ (U), ⵍ (L).",
                    academicTipAr = "كلمة «ⴰⵣⵓⵍ» تتكون من 4 حروف: ⴰ (أ)، ⵣ (زاي ياز)، ⵓ (واو)، ⵍ (لام)."
                ),
                ExerciseStep.MultipleChoice(
                    id = "u1_l1_s2",
                    prompt = "What is the Amazigh word for 'Hello'?",
                    promptSubtext = "ما هي التحية الأمازيغية لـ «مرحباً»؟",
                    options = listOf("Azul", "Tanemmirt", "Uhu", "Ih"),
                    correctAnswer = "Azul",
                    explanationEn = "Azul (ⴰⵣⵓⵍ) means Hello or Greetings.",
                    explanationAr = "أزول (ⴰⵣⵓⵍ) تعني مرحباً."
                ),
                ExerciseStep.Spelling(
                    id = "u1_l1_s3",
                    word = wordAzul,
                    targetScript = ScriptType.TIFINAGH,
                    scrambledLetters = listOf("ⵣ", "ⵍ", "ⴰ", "ⵓ"),
                    correctSpelling = "ⴰⵣⵓⵍ"
                ),
                ExerciseStep.IntroduceCard(
                    id = "u1_l1_s4",
                    word = wordTanemmirt,
                    academicTipEn = "Notice the feminine marker: words starting and ending in 'T' (ⵜ...ⵜ) are often feminine in Tamazight!",
                    academicTipAr = "لاحظ قاعدة التأنيث: أغلب الكلمات المؤنثة في الأمازيغية تبدأ وتنتهي بحرف التاء (ⵜ...ⵜ)!"
                ),
                ExerciseStep.MultipleChoice(
                    id = "u1_l1_s5",
                    prompt = "How do you say 'Thank you' in Tamazight?",
                    promptSubtext = "كيف تقول «شكراً» بالأمازيغية؟",
                    options = listOf("Tanemmirt", "Azul", "Ih", "Suref-iyi"),
                    correctAnswer = "Tanemmirt",
                    explanationEn = "Tanemmirt (ⵜⴰⵏⴻⵎⵎⵉⵔⵜ) means Thank you.",
                    explanationAr = "تانميرت (ⵜⴰⵏⴻⵎⵎⵉⵔⵜ) تعني شكراً."
                ),
                ExerciseStep.MatchPairs(
                    id = "u1_l1_s6",
                    pairs = listOf(
                        MatchPairItem("p1", "Azul", "ⴰⵣⵓⵍ", "Hello"),
                        MatchPairItem("p2", "Tanemmirt", "ⵜⴰⵏⴻⵎⵎⵉⵔⵜ", "Thank you")
                    )
                )
            )
        )
    }

    private fun createUnit1Lesson2(): Lesson {
        val wordIh = Word(id = "w_ih", english = "Yes", arabic = "نعم", tamazight = "Ih", tifinagh = "ⵉⵀ")
        val wordUhu = Word(id = "w_uhu", english = "No", arabic = "لا", tamazight = "Uhu", tifinagh = "ⵓⵀⵓ")
        val phraseSurefIyi = Phrase(
            id = "p_suref",
            english = "Excuse me / Forgive me",
            arabic = "اعذرني / سامحني",
            tamazight = "Suref-iyi",
            tifinagh = "ⵙⵓⵔⴼ-ⵉⵢⵉ"
        )

        return Lesson(
            id = "unit_1_lesson_2",
            unitId = "unit_1",
            order = 2,
            titleEn = "Yes, No & Politeness",
            titleAr = "نعم، لا، والاستئذان",
            descriptionEn = "Essential conversational responses.",
            descriptionAr = "كلمات الرد الأساسية في المحادثة.",
            xpReward = 15,
            steps = listOf(
                ExerciseStep.IntroduceCard(
                    id = "u1_l2_s1",
                    word = wordIh,
                    academicTipEn = "In northern dialects you might also hear 'Wah', but 'Ih' (ⵉⵀ) is standard.",
                    academicTipAr = "في بعض المناطق تقال أيضاً «واه»، ولكن «إيه» (ⵉⵀ) هي الشائعة والمعيارية."
                ),
                ExerciseStep.IntroduceCard(
                    id = "u1_l2_s2",
                    word = wordUhu,
                    academicTipEn = "'Uhu' (ⵓⵀⵓ) is the universal word for 'No'.",
                    academicTipAr = "«أوهو» (ⵓⵀⵓ) هي الكلمة الصريحة لقول «لا»."
                ),
                ExerciseStep.MultipleChoice(
                    id = "u1_l2_s3",
                    prompt = "Select the word for 'Yes':",
                    promptSubtext = "اختر كلمة «نعم»:",
                    options = listOf("Ih (ⵉⵀ)", "Uhu (ⵓⵀⵓ)", "Azul (ⴰⵣⵓⵍ)", "Tanemmirt (ⵜⴰⵏⴻⵎⵎⵉⵔⵜ)"),
                    correctAnswer = "Ih (ⵉⵀ)"
                ),
                ExerciseStep.IntroduceCard(
                    id = "u1_l2_s4",
                    phrase = phraseSurefIyi,
                    academicTipEn = "Compound verb: 'Suref' (forgive) + '-iyi' (me).",
                    academicTipAr = "تركيب الكلمة: «سورف» (سامح) + «إيي» (ياء المتكلم = ني)."
                ),
                ExerciseStep.MatchPairs(
                    id = "u1_l2_s5",
                    pairs = listOf(
                        MatchPairItem("p1", "Ih", "ⵉⵀ", "Yes"),
                        MatchPairItem("p2", "Uhu", "ⵓⵀⵓ", "No"),
                        MatchPairItem("p3", "Suref-iyi", "ⵙⵓⵔⴼ-ⵉⵢⵉ", "Excuse me")
                    )
                )
            )
        )
    }

    private fun createUnit1Lesson3(): Lesson {
        return Lesson(
            id = "unit_1_lesson_3",
            unitId = "unit_1",
            order = 3,
            titleEn = "Building Sentences",
            titleAr = "تكوين الجمل البسيطة",
            descriptionEn = "Assemble full phrases in Amazigh.",
            descriptionAr = "تركيب جمل مفيدة في التخاطب اليومي.",
            xpReward = 20,
            steps = listOf(
                ExerciseStep.AssembleSentence(
                    id = "u1_l3_s1",
                    promptTranslationEn = "Hello, thank you!",
                    promptTranslationAr = "مرحباً، شكراً لك!",
                    correctSentenceTamazight = "Azul , tanemmirt !",
                    correctSentenceTifinagh = "ⴰⵣⵓⵍ , ⵜⴰⵏⴻⵎⵎⵉⵔⵜ !",
                    scrambledTokens = listOf("tanemmirt", "uhu", "Azul", "ih"),
                    correctTokenOrder = listOf("Azul", "tanemmirt")
                ),
                ExerciseStep.MultipleChoice(
                    id = "u1_l3_s2",
                    prompt = "Which letter represents 'Z' in Tifinagh (the Amazigh emblem)?",
                    promptSubtext = "أي حرف يمثل حرف الزاي «ياز» في التيفيناغ وهو رمز الهوية؟",
                    options = listOf("ⵣ", "ⴰ", "ⵎ", "ⵜ"),
                    correctAnswer = "ⵣ",
                    explanationEn = "ⵣ (Yaz) is the iconic letter representing free people (Imazighen).",
                    explanationAr = "حرف ⵣ (ياز) يرمز للإنسان الحر في الثقافة الأمازيغية."
                )
            )
        )
    }

    // ==========================================
    // UNIT 2: Numbers & Counting
    // ==========================================
    private fun createUnit2(): LearningUnit {
        return LearningUnit(
            id = "unit_2",
            order = 2,
            titleEn = "Unit 2: Numbers & Counting",
            titleAr = "الوحدة 2: الأعداد والحساب",
            descriptionEn = "Count from 1 to 10 in Tamazight and Tifinagh.",
            descriptionAr = "تعلم العد والأرقام من 1 إلى 10 بالحروف والأرقام الأمازيغية.",
            themeColorHex = 0xFF1565C0, // Royal Blue
            iconName = "pin",
            guidebook = UnitGuidebook(
                titleEn = "Unit 2 Guidebook: Amazigh Counting",
                titleAr = "دليل الوحدة 2: نظام العد الأمازيغي",
                summaryEn = "Tamazight has an authentic base-10 numerical system dating back millennia.",
                summaryAr = "تمتلك الأمازيغية نظام عد عشري أصيل ومتناسق.",
                sections = listOf(
                    GuidebookSection(
                        headingEn = "Numbers 1 to 5",
                        headingAr = "الأرقام من 1 إلى 5",
                        bodyEn = "1 = Yan ( masculine ) / Yat ( feminine ), 2 = Sin / Snat, 3 = Kraḍ, 4 = Kuẓ, 5 = Smmus.",
                        bodyAr = "1: يان (مذكر) / يات (مؤنث)، 2: سين / سنات، 3: كراض، 4: كوز، 5: سموس.",
                        examples = listOf(
                            GuidebookExample("Yan", "ⵢⴰⵏ", "One (1)", "واحد (1)"),
                            GuidebookExample("Sin", "ⵙⵉⵏ", "Two (2)", "اثنان (2)"),
                            GuidebookExample("Kraḍ", "ⴽⵕⴰⴹ", "Three (3)", "ثلاثة (3)")
                        )
                    )
                )
            ),
            lessons = listOf(
                createUnit2Lesson1(),
                createUnit2Lesson2()
            )
        )
    }

    private fun createUnit2Lesson1(): Lesson {
        val w1 = Word(id = "w_yan", english = "One (1)", arabic = "واحد (1)", tamazight = "Yan", tifinagh = "ⵢⴰⵏ")
        val w2 = Word(id = "w_sin", english = "Two (2)", arabic = "اثنان (2)", tamazight = "Sin", tifinagh = "ⵙⵉⵏ")
        val w3 = Word(id = "w_krad", english = "Three (3)", arabic = "ثلاثة (3)", tamazight = "Kraḍ", tifinagh = "ⴽⵕⴰⴹ")

        return Lesson(
            id = "unit_2_lesson_1",
            unitId = "unit_2",
            order = 1,
            titleEn = "Numbers 1, 2, and 3",
            titleAr = "الأرقام 1، 2، و3",
            descriptionEn = "Learn Yan, Sin, and Kraḍ.",
            descriptionAr = "تعلم يان، سين، وكراض.",
            xpReward = 15,
            steps = listOf(
                ExerciseStep.IntroduceCard(id = "u2_l1_s1", word = w1),
                ExerciseStep.IntroduceCard(id = "u2_l1_s2", word = w2),
                ExerciseStep.IntroduceCard(id = "u2_l1_s3", word = w3),
                ExerciseStep.MultipleChoice(
                    id = "u2_l1_s4",
                    prompt = "What does 'Sin' (ⵙⵉⵏ) mean?",
                    promptSubtext = "ماذا يعني الرقم «Sin» (ⵙⵉⵏ)؟",
                    options = listOf("Two (2)", "One (1)", "Three (3)", "Ten (10)"),
                    correctAnswer = "Two (2)"
                ),
                ExerciseStep.MatchPairs(
                    id = "u2_l1_s5",
                    pairs = listOf(
                        MatchPairItem("p1", "Yan", "ⵢⴰⵏ", "1 (One)"),
                        MatchPairItem("p2", "Sin", "ⵙⵉⵏ", "2 (Two)"),
                        MatchPairItem("p3", "Kraḍ", "ⴽⵕⴰⴹ", "3 (Three)")
                    )
                )
            )
        )
    }

    private fun createUnit2Lesson2(): Lesson {
        val w4 = Word(id = "w_kuz", english = "Four (4)", arabic = "أربعة (4)", tamazight = "Kuẓ", tifinagh = "ⴽⵓⵥ")
        val w5 = Word(id = "w_smmus", english = "Five (5)", arabic = "خمسة (5)", tamazight = "Smmus", tifinagh = "ⵙⵎⵎⵓⵙ")

        return Lesson(
            id = "unit_2_lesson_2",
            unitId = "unit_2",
            order = 2,
            titleEn = "Numbers 4 and 5",
            titleAr = "الأرقام 4 و5",
            descriptionEn = "Count up to five.",
            descriptionAr = "أكمل العد حتى الرقم خمسة.",
            xpReward = 15,
            steps = listOf(
                ExerciseStep.IntroduceCard(id = "u2_l2_s1", word = w4),
                ExerciseStep.IntroduceCard(id = "u2_l2_s2", word = w5),
                ExerciseStep.Spelling(
                    id = "u2_l2_s3",
                    word = w5,
                    targetScript = ScriptType.TIFINAGH,
                    scrambledLetters = listOf("ⵙ", "ⵓ", "ⵎ", "ⵙ", "ⵎ"),
                    correctSpelling = "ⵙⵎⵎⵓⵙ"
                ),
                ExerciseStep.MatchPairs(
                    id = "u2_l2_s4",
                    pairs = listOf(
                        MatchPairItem("p1", "Kuẓ", "ⴽⵓⵥ", "4 (Four)"),
                        MatchPairItem("p2", "Smmus", "ⵙⵎⵎⵓⵙ", "5 (Five)")
                    )
                )
            )
        )
    }

    // ==========================================
    // UNIT 3: Family & Introductions
    // ==========================================
    private fun createUnit3(): LearningUnit {
        return LearningUnit(
            id = "unit_3",
            order = 3,
            titleEn = "Unit 3: Family & People",
            titleAr = "الوحدة 3: العائلة والناس",
            descriptionEn = "Talk about parents, siblings, and relationships.",
            descriptionAr = "تعلم أسماء أفراد الأسرة والروابط العائلية بالأمازيغية.",
            themeColorHex = 0xFFD84315, // Terracotta Orange
            iconName = "family_restroom",
            guidebook = UnitGuidebook(
                titleEn = "Unit 3 Guidebook: Family & Possessives",
                titleAr = "دليل الوحدة 3: العائلة وضمائر الملكية",
                summaryEn = "Amazigh possessive pronouns attach to the end of the noun.",
                summaryAr = "ضمائر الملكية في الأمازيغية تلحق بآخر الاسم بسهولة.",
                sections = listOf(
                    GuidebookSection(
                        headingEn = "Possessive: My (-inu)",
                        headingAr = "ياء المتكلم: إينو (-inu)",
                        bodyEn = "To say 'my ...', append '-inu': Baba (father) -> Baba-inu (my father). Gma (brother) -> Gma-inu (my brother).",
                        bodyAr = "لنسب الشيء لنفسك نضيف اللاحقة «إينو» (-inu): بابا -> بابا-إينو (أبي)، غما -> غما-إينو (أخي).",
                        examples = listOf(
                            GuidebookExample("Baba-inu", "ⴱⴰⴱⴰ-ⵉⵏⵓ", "My father", "أبي"),
                            GuidebookExample("Yemma-inu", "ⵢⴻⵎⵎⴰ-ⵉⵏⵓ", "My mother", "أمي")
                        )
                    )
                )
            ),
            lessons = listOf(
                createUnit3Lesson1()
            )
        )
    }

    private fun createUnit3Lesson1(): Lesson {
        val wBaba = Word(id = "w_baba", english = "Father", arabic = "أب", tamazight = "Baba", tifinagh = "ⴱⴰⴱⴰ")
        val wYemma = Word(id = "w_yemma", english = "Mother", arabic = "أم", tamazight = "Yemma", tifinagh = "ⵢⴻⵎⵎⴰ")

        return Lesson(
            id = "unit_3_lesson_1",
            unitId = "unit_3",
            order = 1,
            titleEn = "Parents: Father & Mother",
            titleAr = "الوالدان: الأب والأم",
            descriptionEn = "Meet the family.",
            descriptionAr = "تعرف على الأب والأم في الأمازيغية.",
            xpReward = 15,
            steps = listOf(
                ExerciseStep.IntroduceCard(
                    id = "u3_l1_s1",
                    word = wBaba,
                    academicTipEn = "Baba means father. In Tifinagh: ⴱ (B) + ⴰ (A) + ⴱ (B) + ⴰ (A).",
                    academicTipAr = "بابا تعني الأب، وتكتب بتكرار حرفي الباء والألف: ⴱⴰⴱⴰ."
                ),
                ExerciseStep.IntroduceCard(
                    id = "u3_l1_s2",
                    word = wYemma,
                    academicTipEn = "Yemma means mother. In some regions, 'Inna' is also used.",
                    academicTipAr = "يما تعني الأم، وتستخدم أيضاً «إنّا» في بعض المناطق."
                ),
                ExerciseStep.AssembleSentence(
                    id = "u3_l1_s3",
                    promptTranslationEn = "My father and my mother",
                    promptTranslationAr = "أبي وأمي",
                    correctSentenceTamazight = "Baba d yemma",
                    correctSentenceTifinagh = "ⴱⴰⴱⴰ ⴷ ⵢⴻⵎⵎⴰ",
                    scrambledTokens = listOf("yemma", "d", "Baba", "yan"),
                    correctTokenOrder = listOf("Baba", "d", "yemma")
                ),
                ExerciseStep.MatchPairs(
                    id = "u3_l1_s4",
                    pairs = listOf(
                        MatchPairItem("p1", "Baba", "ⴱⴰⴱⴰ", "Father"),
                        MatchPairItem("p2", "Yemma", "ⵢⴻⵎⵎⴰ", "Mother")
                    )
                )
            )
        )
    }
}
