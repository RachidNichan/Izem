package com.relyvo.izem.model.curriculum

import com.relyvo.izem.model.Phrase
import com.relyvo.izem.model.Word

sealed class ExerciseStep {
    abstract val id: String

    /**
     * Introduction card: Teaching a new vocabulary item or concept before testing it.
     */
    data class IntroduceCard(
        override val id: String,
        val word: Word? = null,
        val phrase: Phrase? = null,
        val academicTipEn: String = "",
        val academicTipAr: String = ""
    ) : ExerciseStep()

    /**
     * Multiple choice: Choose the correct translation or identify the item.
     */
    data class MultipleChoice(
        override val id: String,
        val prompt: String,
        val promptSubtext: String = "",
        val promptAudioUrl: String = "",
        val promptImageUrl: String = "",
        val options: List<String>,
        val correctAnswer: String,
        val explanationEn: String = "",
        val explanationAr: String = ""
    ) : ExerciseStep()

    /**
     * Sentence builder: Tap scrambled word chips in the correct sequence.
     */
    data class AssembleSentence(
        override val id: String,
        val promptTranslationEn: String,
        val promptTranslationAr: String,
        val correctSentenceTamazight: String,
        val correctSentenceTifinagh: String,
        val scrambledTokens: List<String>,
        val correctTokenOrder: List<String>,
        val audioUrl: String = ""
    ) : ExerciseStep()

    /**
     * Spelling / Tifinagh Assembly: Tap letter tiles to spell a word in Tifinagh or Latin.
     */
    data class Spelling(
        override val id: String,
        val word: Word,
        val targetScript: ScriptType = ScriptType.TIFINAGH,
        val scrambledLetters: List<String>,
        val correctSpelling: String
    ) : ExerciseStep()

    /**
     * Pair Matching: Tap corresponding pairs of Amazigh and target language tiles.
     */
    data class MatchPairs(
        override val id: String,
        val pairs: List<MatchPairItem>
    ) : ExerciseStep()

    /**
     * Listening: Listen to the audio and pick the spoken word or sentence.
     */
    data class Listening(
        override val id: String,
        val audioUrl: String,
        val options: List<String>,
        val correctAnswer: String,
        val transcriptionTifinagh: String = "",
        val transcriptionTamazight: String = ""
    ) : ExerciseStep()
}

enum class ScriptType {
    TIFINAGH,
    LATIN
}

data class MatchPairItem(
    val id: String,
    val amazigh: String,
    val tifinagh: String,
    val translation: String,
    val audioUrl: String = ""
)
