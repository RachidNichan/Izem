package com.relyvo.izem.model

sealed class QuizQuestion {
    data class TextQuestion(
        val word: Word,
        val options: List<String>,
        val correctAnswer: String
    ) : QuizQuestion()

    data class ImageQuestion(
        val word: Word,
        val imageUrl: String,
        val options: List<String>,
        val correctAnswer: String
    ) : QuizQuestion()

    data class SpellingQuestion(
        val word: Word,
        val imageUrl: String,
        val correctAnswer: String,
        val scrambledLetters: List<String>
    ) : QuizQuestion()
}

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val score: Int = 0,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val selectedAnswer: String = "",
    val isAnswerChecked: Boolean = false,
    val isCorrect: Boolean = false,
    val spellingGuessedLetters: List<IndexedLetter> = emptyList()
)

data class IndexedLetter(
    val id: Int,
    val char: String
)